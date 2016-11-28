package qz.communication;

import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.usb.util.UsbUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JHidUtilities {

    private static final Logger log = LoggerFactory.getLogger(JHidUtilities.class);

    private static HIDManager manager = getManager();


    private static HIDManager getManager() {
        ClassPathLibraryLoader.loadNativeHIDLibrary();

        try { return HIDManager.getInstance(); }
        catch(IOException e) { log.error("Failed to initialize HID library", e); }

        return null;
    }

    public static List<HIDDeviceInfo> getHidDevices() {
        List<HIDDeviceInfo> devices;

        try { devices = Arrays.asList(manager.listDevices()); }
        catch(IOException e) {
            log.error("Failed to list devices", e);
            devices = new ArrayList<>();
        }

        return devices;
    }

    public static JSONArray getHidDevicesJSON() throws JSONException {
        List<HIDDeviceInfo> devices = getHidDevices();
        JSONArray devicesJSON = new JSONArray();

        for(HIDDeviceInfo device : devices) {
            JSONObject deviceJSON = new JSONObject();

            deviceJSON.put("vendorId", UsbUtil.toHexString((short)device.getVendor_id()))
                    .put("productId", UsbUtil.toHexString((short)device.getProduct_id()))
                    .put("manufacturer", device.getManufacturer_string())
                    .put("product", device.getProduct_string());

            devicesJSON.put(deviceJSON);
        }

        return devicesJSON;
    }

    public static HIDDeviceInfo findDevice(Short vendorId, Short productId) {
        List<HIDDeviceInfo> devices = getHidDevices();
        for(HIDDeviceInfo device : devices) {
            if ((short)device.getVendor_id() == vendorId && (short)device.getProduct_id() == productId) {
                return device;
            }
        }

        return null;
    }

}
