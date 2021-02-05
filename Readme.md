# XYZ Recipes
## Overview
xyzRecipes is a standalone web application we are using to demonstrate
a functioning weekly meal/recipe planner.

## API Overview - Gradle Modules

project xyzRecipes builds as a Java project using SDK 15 via
the Gradle Build system. This project is treated as a multiproject
build allowing for decoupling of components and modularity in the java 
code. 
### xyz.webkit 
### depends on {dbkit, modelkit}
Contains and manages the following services:
- User Session / Permissions
- API Endpoint Routing (URL Index Based)
- View / Template HTML5 Return 

### xyz.dbkit
### depends on {modelkit}
- Hosts application Database via ID (K,V) Store 
- Writes / Reads Database to (K,V) JSON Store 
- Write Database Query's to JSON Objetct 
- Writes Models in Static Memory to ID (K,V) Store -> JSON 

### xyz.modelkit
#### depends on {}
- Presents Model interface - Writes to K,V Maps
- Writes/Reads Models in K,V Map Stores
- Writes/Reads JSON Format
- Manages ID / Parent ID / Relational Properties

## graphql
- Antlr4 Generated Parse Grammar - Available For Future Extensions
- Includes Pre - Post token entry parse handlers allow building of lexical tree
- We can write JSON parse grammar with antlr or use javax.json api




