# XYZ Recipes
## Overview
xyzRecipes is a standalone web application we are using to demonstrate
a fully functioning weekly meal/recipe planner. This is mean't to be a 
common productivity tool. 

## API Overview
(P) primary feature; (E) Extension feature planned
### xyz.webkit 
###depends on {dbkit, modelkit}
Contains and manages the following services:
- User Session / Permissions (P)
- API Endpoint Routing (GraphQL Index Endpoint Request) (E)
- API Endpoint Routing (URL Index Based) (P)
- View / Template HTML5 Return (P)
- Open Java Server Listens http:// requests feeds (P)

### xyz.dbkit
###depends on {modelkit}
- Hosts application Database via ID (K,V) Store (P)
- Writes / Reads Database to (K,V) JSON Store (P)
- Write Database Query's to JSON Objetct (P)
- Writes Models in Static Memory to ID (K,V) Store -> JSON (P)
- Writes / Reads / Query Database in GraphQL (E)

### xyz.modelkit
####depends on {}
- Presents Model interface - Writes to K,V Maps
- Writes/Reads Models in K,V Map Stores
- Writes/Reads JSON Format
- Manages ID / Parent ID / Relational Properties



