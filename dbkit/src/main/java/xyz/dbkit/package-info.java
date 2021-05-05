/**
 * Package xyz.dbkit provides a mechanism for handling JSONObject file stores on the local
 * system. DBKit applications are coupled with the xyz.modelkit package for providing the base
 * Store/Query functionality. DBKit applications should be Run as their own internal threads with
 * database instances updating during the threaded Run() - RunSync() routines. DBNodes are mirrored
 * in their own model.keys filestore located on the main application entry point app/resources directory.
 * DBNodes are mapped to these Model.keys files.
 */
package xyz.dbkit;