            UDP Client/Server Networking Project
            ====================================

==================================================================
                            Overview
------------------------------------------------------------------
There are two aspects to this project, the server and the client.
They each operate independently but communicate using a UDP 
connection. There will be an executable file for each, but the 
UDPClient executable is the only one that requires command line 
arguements when run. For proper operation, run the UDPServer 
executable before running a UDPClient instance. Without the server
running, attempts to launch a client will succeed but will lockup
the client instance and will require the client process to 
terminate.
==================================================================

==================================================================
                    UDPClient CLI Instructions
------------------------------------------------------------------
There are two ways to execute the UDPClient executable:
	1. Manual Mode
	2. Auto Mode

Manual Mode:
Requires the user to enter their user name and all
math equations manualy during the execution of the program 
instance. The user will also be required to terminate the program
using the "exit" command. The arguement required for the CLI are 
-m to designate manual mode. Example format shown below.

	Manual Mode CLI Format : 
		UDPClient.exe -m

Auto Mode:
Enables the program to generate the math equations
automatically and send the requests to the server at random 
intervals between 0-5 seconds. The program instance will send a 
termate connection request after the 3rd math equation has been 
sent. Upon receiving confirmation from the server that the 
termination request was recieved, the client instance will 
terminate it's execution. The arguements required for the CLI are 
-a to designate auto mode and "user name" to retrieve the username
for the instance. Example format shown below. 

	Auto Mode CLI Format : 
		UDPClient.exe -a "user name" 
==================================================================

==================================================================
                       Makefile Information
------------------------------------------------------------------

==================================================================