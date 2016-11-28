package qz.communication;

import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceInfo;

import javax.usb.util.UsbUtil;
import java.io.IOException;

public class JHidIO implements DeviceIO {

    private HIDDeviceInfo device;
    private HIDDevice openDevice;

    private boolean streaming;


    public JHidIO(Short vendorId, Short productId) throws DeviceException {
        this(JHidUtilities.findDevice(vendorId, productId));
    }

    public JHidIO(HIDDeviceInfo device) throws DeviceException {
        if (device == null) {
            throw new DeviceException("HID device could not be found");
        }

        this.device = device;
    }

    public void open() throws DeviceException {
        if (openDevice == null) {
            try { openDevice = device.open(); }
            catch(IOException e) { throw new DeviceException(e); }
        }
    }

    public boolean isOpen() {
        return openDevice != null;
    }

    public void setStreaming(boolean active) {
        streaming = active;
    }

    public boolean isStreaming() {
        return streaming;
    }

    public String getVendorId() {
        return UsbUtil.toHexString((short)device.getVendor_id());
    }

    public String getProductId() {
        return UsbUtil.toHexString((short)device.getProduct_id());
    }

    public byte[] readData(int responseSize, Byte unused) throws DeviceException {
        byte[] response = new byte[responseSize];

        try {
            int read = openDevice.read(response);
            if (read == -1) {
                throw new DeviceException("Failed to read from device");
            }
        }
        catch(IOException e) {
            throw new DeviceException(e);
        }

        return response;
    }

    public void sendData(byte[] data, Byte unused) throws DeviceException {
        try {
            int wrote = openDevice.write(data);
            if (wrote == -1) {
                throw new DeviceException("Failed to write to device");
            }
        }
        catch(IOException e) {
            throw new DeviceException(e);
        }
    }

    public void close() throws DeviceException {
        if (isOpen()) {
            try { openDevice.close(); }
            catch(IOException e) { throw new DeviceException(e); }
        }

        streaming = false;
    }

}
