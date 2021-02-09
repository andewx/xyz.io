# xyz.modelkit todo

##BRANCH NOTES
### Working Branch
- modelkit
- master -> development -> modelkit (fork path)

## Overall
- Remove unused dependencies from grade build
- Amplify test cases
- Create reference API md file

## UNDEFINED BEHAVIOR
- Cyclical Model Dependencies (Will Stall)
- No Error Handling
- Writes JSON w/ no whitespace - browser json parsers?
- Undefined runtime behavior for large JSON stores. Need benchmarking

## MODEL OBJECTS
- Ensure imports of nested Model Objects work correctly
- Create Standard Model Types {User, Recipe, RecipeItem ...(useful http items)
- API Usage Documentation
- Javadoc API
- Cycle Detection

````
//Currently New Models extend ModelObject
//All new model items should override constructors

public class Recipe extends ModelObject{

//Must register new props
public Recipe(){...} 
public Recipe(String json){...}
public Recipe(int size){...}

public void update(){...} //Must re-register props
}
````

- This property code is fairly boilerplate
- A Java Annotations Update could simplify this essentially would make this into a spring framework


## MODEL UTILS
- Useful for genUID(). Gen UID is not guaranteed unique but forms a hash on the basis of a sequence of random integers. This should be good enough.
- Might make sense to move into utilities module

## EVOLUTION TASKS
- Overall Project Benchmarking Tools
- ModelKit Module as Threaded Runtime for Large Data Stores
- GraphQL Compatibility
- HTTP Method Implementation
