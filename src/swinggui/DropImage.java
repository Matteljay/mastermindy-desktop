// DropImage - MIT License 20190426 Matteljay
package swinggui;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class DropImage extends JPanel implements MouseMotionListener {
	
	private static final long serialVersionUID = 7182982080173030644L;
	private int groupID;
	private DragImage dragImage;
	private BufferedImage image, scaledImage;
	private Dimension previousSize;
	private Container savedParam_container;
	private BufferedImage savedParam_image;
	private int savedParam_carryID;
	private DragImageEvent dragImageEvent;
	
	public DropImage(int groupID) {
		this.groupID = groupID;
		setLayout(new GridBagLayout()); // centers the dragImage
		setPreferredSize(new Dimension(0, 0));
		dragImage = null;
		previousSize = new Dimension(0, 0);
		dragImageEvent = null;
        addMouseMotionListener(this);
	}
	
	public void createDragImage(Container dragContainer, BufferedImage image, int carryID) {
		this.image = image;
		DragImage comp = new DragImage(dragContainer, image, groupID, carryID);
		addDragImage(comp);
		savedParam_container = dragContainer;
		savedParam_image = image;
		savedParam_carryID = carryID;
	}

	public void addDragImage(DragImage comp) {
		dragImage = comp;
		dragImage.setDropSite(this);
		image = dragImage.getImage();
		add(dragImage);
		resizeFilled();
	}
	
	public void addDragImageEvent(DragImageEvent simpleEvent) {
		this.dragImageEvent = simpleEvent;
	}
	
	public void cloneDragImageFrom(DropImage other) {
		image = bufImageCopy(other.savedParam_image);
		DragImage comp = new DragImage(other.savedParam_container, image, groupID, other.savedParam_carryID);
		addDragImage(comp);
	}
	
  	private BufferedImage bufImageCopy(BufferedImage sourceImage) {
		ColorModel cm = sourceImage.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = sourceImage.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
  	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
	    g2d.setColor(getBackground());
	    g2d.fillRect(0, 0, getWidth(), getHeight());
		if(previousSize.equals(getSize()) == false) {
			previousSize = new Dimension(getSize());
			if(dragImage != null) {
				resizeFilled();
			}
		}
	}
	
	public void resizeFilled() {
		resizeFilledTo(getSize());
		validate(); // resets child position according to layout
	}
	
	public void resizeFilledTo(Dimension newSize) {
		Dimension scaledDims = getScaledDimensionFixedAspect(new Dimension(image.getWidth(), image.getHeight()), newSize);
		scaledImage = resizeImage(image, scaledDims);
		dragImage.setScaledImage(scaledImage);
		dragImage.setPreferredSize(scaledDims);
		dragImage.setSize(scaledDims);
	}
	
	private Dimension getScaledDimensionFixedAspect(Dimension imageSize, Dimension boundary) {
	    double widthRatio = boundary.getWidth() / imageSize.getWidth();
	    double heightRatio = boundary.getHeight() / imageSize.getHeight();
	    double ratio = Math.min(widthRatio, heightRatio);
	    int width = Math.max(1, (int)(imageSize.width * ratio));
	    int height = Math.max(1, (int)(imageSize.height * ratio));
	    return new Dimension(width, height);
	}
	
	private BufferedImage resizeImage(BufferedImage originalImage, Dimension newDim) {
		Image scaledImg = originalImage.getScaledInstance(newDim.width, newDim.height, BufferedImage.SCALE_SMOOTH);
		BufferedImage newImage = new BufferedImage(newDim.width, newDim.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = newImage.getGraphics();
		g.drawImage(scaledImg, 0, 0, null);
		g.dispose();
		return newImage;
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		if(e.getID() == MouseEvent.MOUSE_CLICKED) {
			clickTriggered();
		}
		super.processMouseEvent(e);
	}
	
	public Boolean startDrag() {
		return (dragImageEvent == null) ? true : dragImageEvent.allowDrag(this);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}
	
	public int getGroup() {
		return groupID;
	}
	
	public Boolean isFilled() {
		return (dragImage != null);
	}
	
	public DragImage getFilled() {
		return dragImage;
	}
	
	public void detachDragImage() {
		dragImage.setDropSite(null);
		image = null;
		dragImage = null;
		revalidate();
		repaint();
	}
	
	public void removeDragImage() {
		if(dragImage != null) {
			remove(dragImage);
			detachDragImage();
		}
	}
	
	public void dragTriggered() {
		detachDragImage();
		if(dragImageEvent != null) {
			dragImageEvent.startedDrag(this);
		}
	}
	
	public void clickTriggered() {
		if(dragImageEvent != null) {
			dragImageEvent.clicked(this);
		}
	}	
}
