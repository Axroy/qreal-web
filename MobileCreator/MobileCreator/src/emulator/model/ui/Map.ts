import mControl = module("emulator/model/ui/Control");
import mMapTag = module("emulator/model/attributes/MapTag");

export class Map extends mControl.Control {

    private map: Microsoft.Maps.Map;
    get Map(): Microsoft.Maps.Map {
        return this.map
    }

    constructor(tag: mMapTag.MapTag);
    constructor(tag: mMapTag.MapTag, $control: JQuery);
    constructor(tag: mMapTag.MapTag, $control?: JQuery = $("<div></div>")) {
        super(tag, $control);
        this.setDimensions($control);
        $control.css('position', 'relative');

        this.map = new Microsoft.Maps.Map($control.get()[0],
                           {
                               credentials: "AvJwCah4br6cge458C1Vc6NSyzZy2SfsqzBrTwUVmuybDrtrc6pV-qCP98ZkTqKW",
                               center: new Microsoft.Maps.Location(59.876984, 29.839293),
                               mapTypeId: Microsoft.Maps.MapTypeId.road,
                               zoom: 8
                           });


        var points = [];
        points.push(new Point(59.9, 29.9));
        points.push(new Point(60, 29.9));
        points.push(new Point(59.9, 30));
        points.push(new Point(60, 30));
        points.push(new Point(59.8, 30));
        this.addPushpins(points);
    }

    public addPushpins(points: Point[]): void {
        points.map(point => this.addPushpin(point))
    }

    public addPushpin(point: Point) {
        var pushpin = new Microsoft.Maps.Pushpin(new Microsoft.Maps.Location(point.Latitude, point.Longitude), null);
        this.map.entities.push(pushpin);
    }
}

export class Point {
    public Latitude: number;
    public Longitude: number;

    constructor(latitude: number, longitude: number) {
        this.Latitude = latitude;
        this.Longitude = longitude;
    }

}
