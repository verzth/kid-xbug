// XBugService.aidl
package id.kiosku.xbug;

// Declare any non-default types here with import statements
import id.kiosku.xbug.DataBug;

interface XBugManager {
    oneway void send(in DataBug data);
    oneway void screenOn();
    oneway void screenOff();
    oneway void screenToggle();
}