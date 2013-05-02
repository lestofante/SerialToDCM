package reader.seriale;
import java.io.File;
import java.lang.reflect.Field;

public class RXTXPathSetter {

        /**
     * Modify java.library.path to include the path to the appropriate RXTX
     * library - differs based on the OS, but here we cover Windows, Mac and
     * Linux, both 32 and 64 bit for each system.
     *
     * @return true if the path was set successfully, false otherwise.
     */
    public static boolean setPaths() {
        String s;
        if(System.getProperty("os.name").startsWith("Windows")) {
            //if(is64Bit()) {
            //    s = "Windows64";
            //}
            //else {
                s = "windows";
            //}
        }
        else if(System.getProperty("os.name").startsWith("Mac")) {
            s = "mac";
        }
        else if(System.getProperty("os.name").startsWith("Linux")) {
            //if(is64Bit()) {
            //    s = "Linux64";
            //}
            //else {
                s = "linux";
            //}
        }
        else {
            //LOGGER.log(Level.WARNING, "Couldn''t detect OS version for RXTX initialisation: {0}", System.getProperty("os.name"));
            return false;
        }
        System.out.println("path native: "+System.getProperty("java.library.path") + File.pathSeparator + s);
        System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s);
        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        }
        catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            //LOGGER.log(Level.WARNING, "Couldn't initialise RXTX native path.", ex);
            return false;
        }
        //LOGGER.log(Level.INFO, "Initialised RXTX Java path successfully for {0}. java.library.path is now \"{1}\"", new Object[]{System.getProperty("os.name"), System.getProperty("java.library.path")});
        return true;
    }

    /**
     * Determine if the current OS is 64 bit.
     *
     * @return true if it's 64 bit, false if 32.
     */
    //private static boolean is64Bit() {
    //    return System.getProperty("os.arch").contains("64");
    //}
}
