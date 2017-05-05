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

4/17:  
ZoneDecider is now also an online Q-learner MLP using CityData (includes RCI demands) to learn other city factors that could influence zoning decisions.
Addtionally the main AI class is now also a Q-learner MLP that learns how much weight to place on TileMapper or ZoneMapper values (given ZoneAction) instead of taking the average of the two.  Though this was an effective placeholder.  
Since the Q-values are attributed to each cell on the game-world, it is also possible to extract the Q-scores for a given world-state (given an action).  This leads to the new Policy-Map overlay selectable on the GUI.  When a user selects a Zone (R, C, I, or 0) and picks this overlay, the AI system computes a Q-map based on the selected zone, as if it were an output from the ZoneDecider.  This can be an assistive tool for players.  The AI system can indicate by color the tiles most appropriate for the selected zone type.  
Here is the AI-Policy map with zone-R selected:
![policymap_v06_cropped_1200x900](https://cloud.githubusercontent.com/assets/2634337/25106448/7db97b40-2397-11e7-8cae-9b982abaa994.gif)


5/4:
PolicyMap coloration improved to show best target value (Cyan square(s)) with a grey->magenta scale to indicate gradient from [0.0-1.0).  Additionally several changes to design of NN module allows for new user-facing controls and functionality over tuning the AI system.  Not only can weights be changed, altering the scoring values, but the user can (even mid-game) re-architect the MLP's layers, neuron counts, and activation functions.  Furthermore training-error rates and max training cycles (to avoid infinite loop where training does not converge), as well as observation-act cycle times (span of turns to account for the Qactual(s,a) to train against.  The observation Radius allows the local-mapper MLP's to observe larger tile-neighborhoods in their input vectors.  This allows mappers to take into account more neighbor's from further away (at the sacrifice of performance/speed).  

![new interface controls, snapshot](https://cloud.githubusercontent.com/assets/2634337/25732884/2e9779c0-3122-11e7-9383-2fea75533ed0.png)

![Some mapping over a game, autonomous AI mode](https://cloud.githubusercontent.com/assets/2634337/25732762/ff232fbe-3120-11e7-8f9b-e1bb18d9f9b5.gif)
