/**
 * Created by vladimir-zakharov on 10.10.14.
 */
interface DiagramNode extends DiagramElement {
    getX(): number;
    getY(): number;
    getImagePath(): string;
}
