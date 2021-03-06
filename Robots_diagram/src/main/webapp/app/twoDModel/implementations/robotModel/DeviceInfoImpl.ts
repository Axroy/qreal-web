class DeviceInfoImpl implements DeviceInfo {
    private static createdInfos: {string?: DeviceInfo} = {};
    private name: string;
    private friendlyName: string;
    private deviceType;

    constructor(deviceType) {
        this.deviceType = deviceType;
        this.name = deviceType.name;
        this.friendlyName = deviceType.friendlyName;
    }

    static getInstance(deviceType): DeviceInfo {
        if (!DeviceInfoImpl.createdInfos[deviceType.name]) {
            var deviceInfo: DeviceInfo = new DeviceInfoImpl(deviceType);
            DeviceInfoImpl.createdInfos[deviceType.name] = deviceInfo;
            return deviceInfo;
        } else {
            return DeviceInfoImpl.createdInfos[deviceType.name];
        }
    }

    static fromString(str: string): DeviceInfo {
        if (!DeviceInfoImpl.createdInfos[str]) {
            throw new Error("DeviceInfo for " + str + " not found");
        } else {
            return DeviceInfoImpl.createdInfos[str];
        }
    }

    getName(): string {
        return this.name;
    }

    getFriendlyName(): string {
        return this.friendlyName;
    }

    getType() {
        return this.deviceType;
    }

    isA(type): boolean {
        var currentParent = this.deviceType;

        while(currentParent && currentParent !== type) {
            currentParent = currentParent.parentType;
        }

        return currentParent != undefined;
    }
}