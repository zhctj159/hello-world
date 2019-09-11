package zhc.pattern.abstractfactory;

public class ShapeFactory extends AbstractFactory {

	@Override
	public Color getColor(String color) {
		return null;
	}

	@Override
	public Shape getShape(String shape) {
		if (null==shape) {
			return null;
		}
		if (shape.equalsIgnoreCase("circle")) {
			return new Circle();
		} else if (shape.equalsIgnoreCase("SQUARE")) {
			return new Square();
		}
		return null;
	}

}
