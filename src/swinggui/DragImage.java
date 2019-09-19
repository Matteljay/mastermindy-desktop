// DragImage - MIT License 20190426 Matteljay
package swinggui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;

import javax.swing.*;

public class DragImage extends JComponent implements MouseMotionListener {

	private static final long serialVersionUID = -7891247671841491641L;
	private int carryID, groupID;
	private BufferedImage image, scaledImage, scaledImageLight;
  	private Container root, dragContainer;
  	private Point startingPt;
  	private Point deltaPt;
	private boolean hasDragged;
	private boolean mouseInside;
	private DropImage dropSite = null;
	private long prevDrawTime = 0;
	
  	public DragImage(Container dragContainer, BufferedImage image, int groupID, int carryID) {
  		this.dragContainer = dragContainer;
        this.image = image;
        this.scaledImage = null;
  		this.groupID = groupID;
  		this.carryID = carryID;
  		startingPt = new Point(0, 0);
        deltaPt = new Point(0, 0);
        mouseInside = false;
  		setLocation(startingPt);
        addMouseMotionListener(this);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
  	}
	
  	@Override
  	public void addNotify() {
  		super.addNotify();
  		root = getRootPane();
  	}
  	
	public void setScaledImage(BufferedImage scaledImage) {
		this.scaledImage = scaledImage;
		scaledImageLight = bufImageCopy(scaledImage);
		RescaleOp rescaleOp = new RescaleOp(1.2f, 15, null);
		rescaleOp.filter(scaledImageLight, scaledImageLight);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(dragContainer.isShowing() == false) {
			// this instance is getting painted while the parent panel is swapped out/hidden
			removeYourself();
			return;
		}
		if(scaledImage == null) {
			return;
		}
		Graphics2D g2d = (Graphics2D) g;
		if(mouseInside && !hasDragged) {
			g2d.drawImage(scaledImageLight, 0, 0, this);
		} else {
			g2d.drawImage(scaledImage, 0, 0, this);
		}
	}
  	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		if(e.getID() == MouseEvent.MOUSE_PRESSED) {
			mousePressed(e.getPoint());
		} else if(e.getID() == MouseEvent.MOUSE_RELEASED) {
			mouseReleased();
		} else if(e.getID() == MouseEvent.MOUSE_CLICKED) {
			if(hasDropSite()) {
				dropSite.clickTriggered();
			}
		} else if(e.getID() == MouseEvent.MOUSE_ENTERED) {
			mouseInside = true;
			revalidate();
			repaint();
		} else if(e.getID() == MouseEvent.MOUSE_EXITED) {
			mouseInside = false;
			revalidate();
			repaint();
		}
		super.processMouseEvent(e);
	}
	
	@Override
	public void processMouseMotionEvent(MouseEvent e) {
		if(e.getID() == MouseEvent.MOUSE_DRAGGED) {
			myMouseDragged();
		}
		super.processMouseMotionEvent(e);
	}
	
	private void mousePressed(Point relativePt) {
		deltaPt = relativePt;
		hasDragged = false;
	}
	
	private void mouseReleased() {
		if(hasDragged == true) {
			Point imageCenterPt = new Point(getWidth() / 2, getHeight() / 2);
			scanAndLockToDropSite(imageCenterPt);
			if(hasDropSite() == false) {
				removeYourself();
			}
			hasDragged = false;
		}
	}
	
	private void scanAndLockToDropSite(Point imageCenterPt) {
		Point rootPt = SwingUtilities.convertPoint(this, imageCenterPt, dragContainer);
		Component maybeDropImage = SwingUtilities.getDeepestComponentAt(dragContainer, rootPt.x, rootPt.y);
		if(maybeDropImage instanceof DropImage) {
			DropImage dropImage = (DropImage) maybeDropImage;
			if(dropImage.getGroup() == groupID && dropImage.getFilled() == null) {
				lockToDropSite(dropImage);
				return;
			}
		}
	}
	
	private void lockToDropSite(DropImage dropImage) {
		dropImage.addDragImage(this);
		dropSite = dropImage;
		root.revalidate();
		root.repaint();
	}
	
	private void myMouseDragged() {
		if(hasDragged == false) {
			initialDragAction();
		} else {
			if(System.currentTimeMillis() - prevDrawTime < 40) { // spam protect
				return;
			}
			prevDrawTime = System.currentTimeMillis();
			updateDragLocation();
		}
	}
	
	private void initialDragAction() {
		if(dropSite.startDrag() == true) {
			updateDragLocation();
			root.setComponentZOrder(this, 0); // performs remove() & add()
			hasDragged = true;
			dropSite.dragTriggered();
		}
	}

	private void updateDragLocation() {
		Point mousePt = MouseInfo.getPointerInfo().getLocation();
		Point imageAbsPt = subtract(mousePt, deltaPt);
		Point imageAbsSizePt = new Point(imageAbsPt.x + getWidth(), imageAbsPt.y + getHeight());
		Point absContainerPt = dragContainer.getLocationOnScreen();
		Point absContainerSizePt = new Point(absContainerPt.x + dragContainer.getWidth(), absContainerPt.y + dragContainer.getHeight());
		Point newImageAbsPt = new Point(imageAbsPt);
		if(imageAbsPt.x <= absContainerPt.x) {
			newImageAbsPt.x = absContainerPt.x;
		}
		if(imageAbsPt.y <= absContainerPt.y) {
			newImageAbsPt.y = absContainerPt.y;
		}
		if(imageAbsSizePt.x >= absContainerSizePt.x) {
			newImageAbsPt.x = absContainerSizePt.x - getWidth();
		}
		if(imageAbsSizePt.y >= absContainerSizePt.y) {
			newImageAbsPt.y = absContainerSizePt.y - getHeight();
		}
		Point newImageRelPt = subtract(newImageAbsPt, root.getLocationOnScreen());
		setLocation(newImageRelPt); // relative to root
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {}
	
	@Override
	public void mouseMoved(MouseEvent e) {}
	
	private void removeYourself() {
		root.remove(this);
		root.revalidate();
		root.repaint();
	}

	private Point subtract(Point one, Point two) {
        return new Point(one.x - two.x, one.y - two.y);
    }
	
  	private BufferedImage bufImageCopy(BufferedImage sourceImage) {
		ColorModel cm = sourceImage.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = sourceImage.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
  	}
	
	public Boolean hasDropSite() {
		return (dropSite != null);
	}
	
	public void setDropSite(DropImage dropImage) {
		dropSite = dropImage;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public int getID() {
		return carryID;
	}
}