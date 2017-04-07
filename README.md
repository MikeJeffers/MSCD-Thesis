# MSCD-Thesis
Prototype for master's thesis

![Travis-CI](https://travis-ci.org/MikeJeffers/MSCD-Thesis.svg?branch=master)


![Current Work](https://cloud.githubusercontent.com/assets/2634337/23097028/a03b9e64-f5f7-11e6-8dbc-cb1df8adb815.gif)

Here you can see some of the early features of the game and complexities of certain buildings, the effects they produce, and the effects they have on the growth of their neighbors!

Updates 4/7/17:  
Now we have a rudimentary AI system composed of a few Multi-layer Perceptrons that act on a few isolated problems to formulate a move for each AI turn cycle (right now: hard-coded as 5 turn minimum wait between consecutive AI actions).  
ZoneDecider - picks zone for the Action  
TileMapper - examines Tile properties, current and base values, as well as current Zoning density-> returns a map of values where high values indicate better places for Action to occur (given zone)  
ZoneMapper - examines Zones at each Tile and determines value of changing that zone to Action's Zone choice-> returned as map of values  
This all comes together, where the map value is found to be highest, the move is made.
This is a Q-learning system, that is trained online by its own moves.  The MLP's model the Q function, with the exception of the ZoneDecider which is currently trained offline as a supervised MLP.
The Mappers are creating a Q-value map given the Zone Action, with a current State.  Following the Q(s,a) form.  

See it in action here (AI only):
![AI playing game solo](https://cloud.githubusercontent.com/assets/2634337/24819773/0a195a06-1bb4-11e7-99de-ac70f52b815a.gif)
