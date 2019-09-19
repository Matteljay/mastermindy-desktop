package swinggui;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class JImage extends JComponent {

	private static final long serialVersionUID = 5508299982866049760L;
	private BufferedImage image, scaledImage;
	Dimension previousSize, imageSize;

	public JImage(BufferedImage image) {
		this.image = image;
		imageSize = new Dimension(image.getWidth(), image.getHeight());
		previousSize = new Dimension(0, 0);
		scaledImage = null;
    }

	@Override
	protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
		if(previousSize.equals(getSize()) == false) {
			previousSize = new Dimension(getSize());
			Dimension scaledDims = getScaledDimensionFixedAspect(imageSize, getSize());
			scaledImage = resizeImage(image, scaledDims);
		}
    	if(scaledImage != null) {
    		g.drawImage(scaledImage, (getWidth() - scaledImage.getWidth()) / 2, (getHeight() - scaledImage.getHeight()) / 2, this);
    	}
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
}
