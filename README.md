Johnny Huang Jobcoin Mixer

Preface* I am not a frontend developer, and have very little front end experience, therefore I opted to create a simple java web ui using Vaadin (Although that learning curve added a bit more time an I wanted). The rest of my time was spent on building the Java portion of this project as there was no Java template project avaliable.


Instructions
Requirements
Java 8
Gradle (wrapper included with project)

How to Run:
./gradlew bootRun
This will compile and start the webserver as well as spin up the UI on port 8080 

Webapp Ui build via vaalin located at http://localhost:8080
Please note that only the mixer portion calls the mixer webapp/controllers hosted on port 8080, the rest of it is basically calling the api provided at https://jobcoin.gemini.com/garbage-renewal.

Understanding of the Goal:
The goal of this project is to grant someone who wants to keep how much crypto currencies (in this case only jobcoins) they own private. The user will obsficate this knowledge by sending their coins to an address that the mixer owns which will then be combined(mixed) with other funds in a central(house) address. Those other funds come from other users who are trying to mix their funds at the same time. After a short amount of time, the central(house) address will move funds to the wallets that the user provided to the mixer.

Breakdown

The Assumption:
	The user has one main wallet with funds and has decided on how much they want to mix
	Prior to using the mixer, the User has created or controls N wallets that they will provide to the mixer.

The Mixer:
	The user tells the mixer about his/her N Addresses to which he/she wants the funds to end up in.
	Once the mixer receives a request, it give the user a newly created deposit address and tell them how long the mixer will wait.

The User:
	Having recieved the deposit address and expiraiton date from the mixer, the can then send their coins to provided address

The Mixer
	The mixer will poll this address for funds every second until the wait period is over. 
	Since Tte Created Destination Address has no funds to start with, it will check the deposit address for a balance greater than 0 to conclude the user is done.
	If the balance of the deposit wallet is greater than zero before the expiration period, the mixer transfer all funds it has at the moment it notices to central/house account.
	The mixer will divide the funds up randomly into N chunks, where N is the amount of address originally provided to the mixer. 
	Those chunks will then get transferred to the destination address the user provided space apart in short intervals of time. 
	The sum all jobcoins sent will total what the user sent to the deposit address.

Main Components
The MixerService: Main class of this webapp. Delegates task to the JobCoinService and TransactionScheduler. Also responsible for breaking ups the transaction into chunks.
The TransactionScheduler: Responsible for scheduling transactions in discrete intervals of time.
The JobCoinService: Is the service that is actually resposible for polling for balances and making transactions happen.

Sample using POSTMAN oppose to using basic UI provided in localhost:8080

User starts off with a dirty wallet address
	request type: POST 
	request uri: http://jobcoin.gemini.com/garbage-renewal/create?address=dirty1

Ask the mixer to mix and send to destination wallets 
	request type: POST 
	request uri: http://localhost:8080/api/mixer/mix
	request body: { "destinations": ["clean1", "clean2"]}

Look at the response Body for deposit address and expiraiton date
	response body will look like: 
	{
	    "depositAddress": "c39130d0-4156-4013-b2c5-b155fef4377b",
	    "expiryDate": "2021-08-24T18:08:48.271"
	}

User sends coins to depositAddress (in the above example it would be c39130d0-4156-4013-b2c5-b155fef4377b)
	request type: POST
	request uri: http://jobcoin.gemini.com/garbage-renewal/api/transactions
	request body: 
	{
	  "fromAddress": "dirty1",
	  "toAddress": "c39130d0-4156-4013-b2c5-b155fef4377b",
	  "amount": 50
	}
	response body: {"status": "OK"}

Query the all transactions endpoint to see the transactions between the "HOUSE ADDRESS" and the destination address
	request type: POST
	request uri: http://jobcoin.gemini.com/garbage-renewal/api/transactions

	will be easier to see from: https://jobcoin.gemini.com/garbage-renewal under the section Transactions. 
	It will take a couple of refreshes because the transactions from "HOUSE ADDRESS" to destination addresses are broken up into chunks and sent a different times in the future.


