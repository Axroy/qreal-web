class SonarSensorItem extends SensorItem {
    private scanningRegion:RaphaelPath;
    private sonarRange = 255;
    private regionStartX:number;
    private regionStartY:number;
    private regionTransformationString = "";

    constructor(robotItem:RobotItem, worldModel:WorldModel, sensorType:DeviceInfo,
                pathToImage:string, position?:TwoDPosition) {
        super(robotItem, worldModel, sensorType, pathToImage, position);
        var paper:RaphaelPaper = worldModel.getPaper();
        var defaultPosition = this.getStartPosition(position);

        this.regionStartX = defaultPosition.x + this.width / 2;
        this.regionStartY = defaultPosition.y + this.height / 2

        var regAngle = 20;
        var halfRegAngleInRad = regAngle / 2 * (Math.PI / 180)

        var rangeInPixels = this.sonarRange * Constants.pixelsInCm;

        var regionTopX = this.regionStartX + Math.cos(halfRegAngleInRad) * rangeInPixels;
        var regionTopY = this.regionStartY - Math.sin(halfRegAngleInRad) * rangeInPixels;

        var regionBottomX = regionTopX;
        var regionBottomY = this.regionStartY + Math.sin(halfRegAngleInRad) * rangeInPixels;

        this.scanningRegion = paper.path("M" + this.regionStartX + "," + this.regionStartY +
        "L" + regionTopX + "," + regionTopY +
        "Q" + (this.regionStartX + rangeInPixels) + "," + this.regionStartY + " " + regionBottomX + "," + regionBottomY +
        "Z");
        this.scanningRegion.attr({fill: "#c5d0de", stroke: "#b1bbc7", opacity: 0.5});
    }

    setStartPosition() {
        super.setStartPosition();
        var regAngle = 20;
        var halfRegAngleInRad = regAngle / 2 * (Math.PI / 180)

        var rangeInPixels = this.sonarRange * Constants.pixelsInCm;

        var regionTopX = this.regionStartX + Math.cos(halfRegAngleInRad) * rangeInPixels;
        var regionTopY = this.regionStartY - Math.sin(halfRegAngleInRad) * rangeInPixels;

        var regionBottomX = regionTopX;
        var regionBottomY = this.regionStartY + Math.sin(halfRegAngleInRad) * rangeInPixels;

        this.scanningRegion.attr({
            path: "M" + this.regionStartX + "," + this.regionStartY +
            "L" + regionTopX + "," + regionTopY +
            "Q" + (this.regionStartX + rangeInPixels) + "," + this.regionStartY + " " + regionBottomX + "," + regionBottomY +
            "Z"
        });

        this.scanningRegion.transform("");
        this.regionTransformationString = "";
    }

    transform(transformationString:string) {
        super.transform(transformationString);
        this.scanningRegion.transform(this.regionTransformationString + transformationString);
    }

    animate(element, transformationString, animation, timestamp):void {
        super.animate(element, transformationString, animation, timestamp);
        var sensorAnimation = Raphael.animation({transform: this.regionTransformationString + transformationString}, timestamp);
        this.scanningRegion.animateWith(element, animation, sensorAnimation);
    }

    stopAnimation():void {
        super.stopAnimation();
        this.scanningRegion.stop();
    }

    updateTransformationString():void {
        super.updateTransformationString();
        this.regionTransformationString = this.scanningRegion.transform();
    }

    rotate(angle:number) {
        super.rotate(angle);

        var regionRotationX = this.image.matrix.x(this.regionStartX, this.regionStartY);
        var regionRotationY = this.image.matrix.y(this.regionStartX, this.regionStartY);

        this.scanningRegion.transform(this.regionTransformationString + "R" + angle + "," +
        regionRotationX + "," + regionRotationY);
    }

    remove():void {
        super.remove();
        this.scanningRegion.remove();
    }
}