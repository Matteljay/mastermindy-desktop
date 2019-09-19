// DragImageEvent - MIT License 20190426 Matteljay
package swinggui;

public interface DragImageEvent {
	public Boolean allowDrag(DropImage dropImage);
	public void startedDrag(DropImage dropImage);
	public void clicked(DropImage dropImage);
}
