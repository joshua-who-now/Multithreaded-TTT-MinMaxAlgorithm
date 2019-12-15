# Project5-CS342-TTT

# Description:
This is your second team project. You will create a server that plays Tic Tac Toe with
each client that connects to that server. The server will utilize the Min-Max algorithm to
determine its moves as well as when the game has been won or tied. At the end of each
game, each client will be able to play again or quit. 

All networking must be done utilizing Java Sockets (as shown in class).
The server must run on its own thread and handle each client on a separate thread. The
client programs must connect to the server on a separate thread. You may not use
libraries or classes not included in the standard Java 8 release.

# Implementation Details:
The client and server programs for this project will be Maven projects. You can use the
Maven templates included in Project #5 on Blackboard. You will provide the following
functionality:

• Each client can see a list of the top 3 highest scores.

• The server will not limit the number of clients connected at one time

• When each client logs on, they will choose if the server plays in “easy”, “medium”
or “expert” mode and play a game of Tic Tac Toe with the server.

• When that game is complete, they can choose to play again or quit. If they play
again, they can change the servers level of expertise to something else if they
want.

To clarify, if there are 6 clients on the server at one time, there would be 6 games on 6
different client threads, playing against the server simultaneously. The client will always
be ‘O’ and the server will always be ‘X’. After each game is complete, the number of
client wins should be updated as well as the list of top 3 scores. Each client should see
the updated list. Otherwise, Clients do not need to know about each other.

You must still utilize the GameInfo class as the only means of passing information
between the server and each client. You must incorporate the Min-Max code provided to
implement the servers Min-Max algorithm. More on that below. 

The Server GUI must have a start up scene to enter the port and start the server. Also a
list view that records the actions and traffic on the server. Feel free to add any other
elements you feel necessary. Also feel free to implement the “look and feel” as you see
fit. 

The client must also have a startup scene that allows the user to enter a port and ip
address to connect to the server. The client program must have a way to choose if the
server will play in “easy”, “medium” or “expert” mode. Also, the client must have a way
to display the current game board, top 3 scores and a “play again” button. Feel free to
add any other elements you feel necessary. Also feel free to implement the “look and
feel” as you see fit. 

# The Min-Max Algorithm:
In the included code, the Min-Max algorithm takes in the current state of the Tic Tac Toe
game. It assumes that the next move is always X. So, you will want to limit your clients
to playing O. This algorithm goes back and forth between two recursive methods,
exploring every possible state of the game for every possible next move by X and
response by O. When it reaches an end state, it will percolate that result up as the
recursion unfolds and give X a score for each possible current move. 

# You should run the code with several different states to understand what it is doing!
Your server will incorporate this code in a class called FindNextMove. In that class, you
will want to take the current state for a game and call the Min-Max algorithm to inform
the server of its next move. The algorithm must run on it’s own thread. Your server will
have a single instance of the FindNextMove class that will be used to determine each
next move for each client game. You will also need to utilize synchronization to avoid 
multiple client threads corrupting the results. For instance, Min-Max returns a list of
moves but it is a combination of moves from multiple games on different threads.

You may only use the Min-Max code provided. You may alter it if you wish to suit your
servers logic. The Min-Max algorithm must run on its own thread and use
synchronization to avoid race conditions.

The algorithm will always return the best move for the server. You may consider this as
“expert” mode. You will have to determine how to use the results of the algorithm for
“easy” and “medium” modes.
