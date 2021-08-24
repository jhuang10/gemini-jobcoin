package com.johnnyhuang.mixer;

import com.johnnyhuang.mixer.client.JobCoinApiClient;
import com.johnnyhuang.mixer.client.MixerClient;
import com.johnnyhuang.mixer.dto.AddressInfoDTO;
import com.johnnyhuang.mixer.dto.MixRequestDTO;
import com.johnnyhuang.mixer.dto.MixResponseDTO;
import com.johnnyhuang.mixer.dto.TransactionDTO;
import com.johnnyhuang.mixer.domain.models.Address;
import com.johnnyhuang.mixer.domain.models.Transaction;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Objects;

/**
 * Creates the Main UI to interface with the backend webapp.
 * Uses vaadin to create the front in java.
 * Can be accessed via http:localhost:8080
 */
@Route
public class MainView extends VerticalLayout {

    @Autowired
    public MainView(JobCoinApiClient jobCoinApiClient, MixerClient mixerClient) {

        addJobCoinTransactionView(jobCoinApiClient);
        addMixerView(mixerClient);
        addTransactionHistoryView(jobCoinApiClient);
        addAddressInfoView(jobCoinApiClient);
    }

    private void addJobCoinTransactionView(JobCoinApiClient jobCoinApiClient) {
        H2 jobCoinTransactionHeading = new H2("Send JobCoins");
        TextField fromAddress = new TextField("From:");
        TextField toAddress = new TextField("To:");
        TextField amount = new TextField("Amount:");

        final Button sendJobCoins = new Button("Send JobCoins",
                e -> {
                    Notification.show(jobCoinApiClient.sendJobCoins(
                            new Transaction(new Address(fromAddress.getValue()),
                                    new Address(toAddress.getValue()),
                                    new Float(amount.getValue()))).block().toString());

                }
        );
        sendJobCoins.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(jobCoinTransactionHeading, fromAddress, toAddress, amount, sendJobCoins);
    }

    private void addMixerView(MixerClient mixerClient) {

        H2 mixJobCoinsHeading = new H2("Mix Your JobCoins");

        TextArea destinationAddress = new TextArea("Destination Address");

        Span mixerAddress = new Span();
        mixerAddress.setVisible(false);
        final Button generateMixAddress = new Button("Generate Mix Address",
                e -> {
                    mixerAddress.removeAll();
                    MixRequestDTO mixRequestDTO = new MixRequestDTO(Arrays.asList(destinationAddress.getValue().split("[, ]")));
                    MixResponseDTO mixResponseDTO = mixerClient.mixJobCoins(mixRequestDTO).block();
                    assert mixResponseDTO != null;
                    mixerAddress.add(String.format("Send to this Mixer Address: %s before %s", mixResponseDTO.getDepositAddress(), mixResponseDTO.getExpiryDate()));
                    mixerAddress.setVisible(true);
                }
        );
        generateMixAddress.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(mixJobCoinsHeading, destinationAddress, generateMixAddress, mixerAddress);
    }

    private void addAddressInfoView(JobCoinApiClient jobCoinApiClient) {

        H2 getAddressInfoHeading = new H2("Get Balance and Transaction History for Address");

        Grid<TransactionDTO> transactionForAddressGrid = new Grid<>(TransactionDTO.class);

        TextField addressInput = new TextField();
        Span span = new Span();
        span.setVisible(false);
        transactionForAddressGrid.setVisible(false);
        final Button fetchAddressInfo = new Button("Fetch",
                e -> {
                    AddressInfoDTO addressInfoDTO = Objects.requireNonNull(jobCoinApiClient.getAddressInfo(new Address(addressInput.getValue())).block());
                    span.setVisible(true);
                    transactionForAddressGrid.setVisible(true);
                    span.setId("Balance");
                    span.add(addressInfoDTO.getBalance().toString());
                    transactionForAddressGrid.setItems(addressInfoDTO.getTransactions());
                }
        );
        fetchAddressInfo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(getAddressInfoHeading, addressInput, fetchAddressInfo, span, transactionForAddressGrid);
    }

    private void addTransactionHistoryView(JobCoinApiClient jobCoinApiClient) {
        H2 getAllTransactionsHeading = new H2("Fetch All Transactions");

        final Grid<TransactionDTO> transactionsGrid = new Grid<>(TransactionDTO.class);
        transactionsGrid.setItems(jobCoinApiClient.getTransactionHistory().toStream());

        final Button fetchAllTransactions = new Button("Refresh",
                e -> transactionsGrid.setItems(jobCoinApiClient.getTransactionHistory().toStream()));
        fetchAllTransactions.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(getAllTransactionsHeading, fetchAllTransactions, transactionsGrid);
    }


}
