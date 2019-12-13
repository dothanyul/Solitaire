# Solitaire
Command line Klondike solitaire game

This game follows the standard rules of Klondike solitaire.

Keyboard controls:
Stacks are numbered 1-7 from left to right. To move a card from one stack to 
another, enter first the number of the origin stack, then the number of the 
destination stack. To move a card from the flip stack, enter 's' for the 
origin stack. To move a card to a top stack, enter 'u' for the destination 
stack. To flip through the flip stack, enter 'f'. To end the game, enter 
'quit'; to display a help message, enter 'help'.

If the player enters an invalid command or a command for an invalid move, an 
appropriate error message is displayed to the right of the board. This will
disappear after the next move is made. 
When all the cards are turned face-up and the flip stack is empty, the game
finishes itself with a delay in between moves.
