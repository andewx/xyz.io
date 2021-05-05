/**
 * This package provides the base ModelObject class which extends JSONObject
 * object representation. ModelObjects map JSONObject key/value pairs to their
 * own internal class fields. When adding in new objects which are intended to be
 * managed by the xyz.dbkit database instance you must update the ModelKeys.java file.
 * Specifically you should add in the ClassName of the newly added ModelObject class
 * and provide a default constructor for that model to the ModelKeys static class.
 */
package xyz.model;