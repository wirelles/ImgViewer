package imageviewer;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;



/**
 * 
 * @author ggrec	
 *
 */
final public class MultiImageViewerComponent extends AEFContainer<MultiImageViewerComponent>
{



	// ==================== 1. Static Fields ========================

	/**
	 * The spacing between images
	 */
	private static final int IMG_SPACING = 10;


	// ====================== 2. Instance Fields =============================

	private Collection<Image> images;
	
//	private Image[] images ;

	final private Collection<Image> screenImages = new ArrayList<>();

	private Canvas canvas;

	private AffineTransform transform = new AffineTransform();


	// ==================== 5. Creators ====================
	
	@Override
	protected void createContent(final Composite parent)
	{
		canvas = new Canvas(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NO_BACKGROUND | SWT.BORDER);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		addListeners();

		initScrollBars();

		updateViewFromModel();
	}


	/**
	 * Initialize the scrollbar and register listeners. 
	 */
	private void initScrollBars() 
	{
		
		
		
		
		final ScrollBar horizontal = canvas.getHorizontalBar();

		horizontal.setVisible(true);
		horizontal.addSelectionListener(new SelectionAdapter() 
		{
			@Override public void widgetSelected(final SelectionEvent event) 
			{
				scrollHorizontally((ScrollBar) event.widget);
			}
		});

		final ScrollBar vertical = canvas.getVerticalBar();

		vertical.setVisible(true);
		vertical.addSelectionListener(new SelectionAdapter() 
		{
			@Override public void widgetSelected(final SelectionEvent event) 
			{
				scrollVertically((ScrollBar) event.widget);
			}
		});
	}


	private void addListeners()
	{
		canvas.addControlListener(new ControlAdapter() 
		{
			@Override public void controlResized(final ControlEvent event) 
			{
				syncScrollBars();
			}
		});

		canvas.addPaintListener(new PaintListener() 
		{ 
			@Override public void paintControl(final PaintEvent event) 
			{
				paint(event.gc);
			}
		});

		canvas.addDisposeListener(new DisposeListener()
		{
			@Override public void widgetDisposed(final DisposeEvent e)
			{
				if (images != null)
					for (final Image image : images)
						image.dispose();

				disposeScreenImages();
			}
		});
	}


	// ==================== 6. Action Methods ====================

	private void updateViewFromModel()
	{
		if (isBuilt())
		{

		}
	}


	private void disposeScreenImages()
	{
		for (final Image image : screenImages)
			image.dispose();
	}


	private void paint(final GC gc)
	{
		if (images != null) 
		{
			disposeScreenImages();
			
			int placeY = 0;
			
			for (final Image sourceImage : images)
			{
				final Rectangle clientRect = canvas.getClientArea(); /* Canvas' painting area */

				Rectangle imageRect = inverseTransformRect(transform, clientRect);

				final int gap = 2; /* find a better start point to render */
				imageRect.x -= gap; 
				imageRect.y -= gap;
				imageRect.width += 2 * gap; 
				imageRect.height += 2 * gap;

				final Rectangle imageBound = sourceImage.getBounds();
				
				imageRect = imageRect.intersection(imageBound);
				
				final Rectangle destRect = transformRect(transform, imageRect);

				final Image screenImage = new Image(canvas.getDisplay(), clientRect.width, clientRect.height);

				final GC newGC = new GC(screenImage);
				newGC.setClipping(clientRect);
				newGC.drawImage(
						sourceImage,
						imageRect.x,
						imageRect.y,
						imageRect.width,
						imageRect.height,
						destRect.x,
						destRect.y,
						destRect.width,
						destRect.height);
				newGC.dispose();

				gc.drawImage(screenImage,0,placeY);
				
				placeY += imageRect.height + IMG_SPACING ;

				screenImages.add(screenImage);
			}
		}
		else 
		{
			final Rectangle clientRect = canvas.getClientArea(); /* Canvas' painting area */

			gc.setClipping(clientRect);
			gc.fillRectangle(clientRect);
			initScrollBars();
		}
	}
//	private void paint(GC gc) 
//	{
//		Rectangle clientRect = canvas.getClientArea(); /* Canvas' painting area */
//		if (images != null){
//			int placeY=0; /* Y point for sourceImage[i] to s*/
//			int espacio=10; /* Gap or space between images*/
//			Image[] im = new Image[images.size()];
//			int j=0;
//			for(Image img : images) 
//			{
//				im[j] = img;
//				j++;
//			}
//			for(int i=0;i<im.length;i++) {
//						placeY+=espacio;
//					
//					Rectangle imageBound = im[i].getBounds();
//					
//					// Ratio of the Image real width with the Canvas width
//					double prop = (double)imageBound.width/clientRect.width;
//                                        //The height of the image displayed.
//					int altoImg = (int)(imageBound.height/prop);
//					
//					//gc.drawImage(sourceImage[i], 0, alto);
//					gc.drawImage(	im[i], 
//									0,
//									0,
//									imageBound.width,
//									imageBound.height,
//									0,
//									placeY,
//									clientRect.width,
//									altoImg
//									);
//					placeY+=altoImg;
//				}
//			
//		}
//		else 
//		{
//			final Rectangle clientRect = canvas.getClientArea(); /* Canvas' painting area */
//			gc.setClipping(clientRect);
//			gc.fillRectangle(clientRect);
//			initScrollBars();
//		}
//		}
//		
	

	private void scrollHorizontally(final ScrollBar scrollBar) 
	{
		if (images == null)
			return;

		final AffineTransform af = transform;
		final double tx = af.getTranslateX();
		final double select = -scrollBar.getSelection();
		af.preConcatenate(AffineTransform.getTranslateInstance(select - tx,0));
		transform = af;
		syncScrollBars();
	}


	private void scrollVertically(final ScrollBar scrollBar) 
	{
		if (images == null)
			return;

		final AffineTransform af = transform;
		final double ty = af.getTranslateY();
		final double select = -scrollBar.getSelection();
		af.preConcatenate(AffineTransform.getTranslateInstance(0,select - ty));
		transform = af;
		syncScrollBars();
	}


	/**
	 * Synchronize the scrollbar with the image. If the transform is out
	 * of range, it will correct it. This function considers only following
	 * factors :<b> transform, image size, client area</b>.
	 */
	private void syncScrollBars()
	{
		if (images == null) 
		{
			canvas.redraw();
			return;
		}

		AffineTransform af = transform;
		final double sx = af.getScaleX(), sy = af.getScaleY();
		double tx = af.getTranslateX(), ty = af.getTranslateY();
		if (tx > 0) tx = 0;
		if (ty > 0) ty = 0;

		final ScrollBar horizontal = canvas.getHorizontalBar();
		horizontal.setIncrement(canvas.getClientArea().width / 100);
		horizontal.setPageIncrement(canvas.getClientArea().width);
		// final Rectangle imageBound = sourceImage.getBounds();
		final Rectangle imageBound = boundsOfAllImages();
		final int cw = canvas.getClientArea().width, ch = canvas.getClientArea().height;

		if (imageBound.width * sx > cw)  /* image is wider than client area */
		{
			horizontal.setMaximum((int) (imageBound.width * sx));
			horizontal.setVisible(true);
			if (((int) - tx) > horizontal.getMaximum() - cw)
				tx = -horizontal.getMaximum() + cw;
		} 
		else /* image is narrower than client area */
		{ 
			 horizontal.setVisible(false);
			tx = (cw - imageBound.width * sx) / 2; //center if too small.
		}

		horizontal.setSelection((int) (-tx));
		horizontal.setThumb((canvas.getClientArea().width));

		final ScrollBar vertical = canvas.getVerticalBar();
		vertical.setIncrement(canvas.getClientArea().height / 100);
		vertical.setPageIncrement((canvas.getClientArea().height));

		if (imageBound.height * sy > ch) /* image is higher than client area */
		{ 
			vertical.setMaximum((int) (imageBound.height * sy));
			vertical.setVisible(true);
			if (((int) - ty) > vertical.getMaximum() - ch)
				ty = -vertical.getMaximum() + ch;
		}
		else /* image is less higher than client area */
		{ 
			/*
			 * spetrila@ansis.eu, 2014.09.02: This prevents the client from freezing when the vertical scrollbar
			 *                                disappears. It is sort of a quick fix, because this whole code is crap
			 *                                (not written by us). It will be refactored at some point.
			 */
//			 vertical.setVisible(false);
			ty = (ch - imageBound.height * sy) / 2; //center if too small.
		}
	
		vertical.setSelection((int) (-ty));
		vertical.setThumb((canvas.getClientArea().height));

		/* update transform. */
		af = AffineTransform.getScaleInstance(sx, sy);
		af.preConcatenate(AffineTransform.getTranslateInstance(tx, ty));
		transform = af;

		canvas.redraw();
	}


	private Rectangle boundsOfAllImages()
	{
		int totalWidth = 0;
		int totalHeight = 0;

		for (final Image image : images)
		{
			final int imgWidth = image.getBounds().width;
			if (imgWidth > totalWidth)
				totalWidth = imgWidth;

			totalHeight += image.getBounds().height ;
		}

		return new Rectangle(-1, -1, totalWidth, totalHeight);
	}


	// ==================== 7. Getters & Setters ====================

	public MultiImageViewerComponent show(final Collection<Image> images)
	{
		this.images = images;
		updateViewFromModel();
		return this;
	}


	// ==================== Static Helper Methods ====================

	/**
	 * Given an arbitrary rectangle, get the rectangle with the given transform.
	 * The result rectangle is positive width and positive height.
	 * @param af AffineTransform
	 * @param src source rectangle
	 * @return rectangle after transform with positive width and height
	 */
	public static Rectangle transformRect(final AffineTransform af, Rectangle src)
	{
		final Rectangle dest= new Rectangle(0,0,0,0);
		src=absRect(src);
		Point p1=new Point(src.x,src.y);
		p1=transformPoint(af,p1);
		dest.x=p1.x; dest.y=p1.y;
		dest.width=(int)(src.width*af.getScaleX());
		dest.height=(int)(src.height*af.getScaleY());
		return dest;
	}

	/**
	 * Given an arbitrary rectangle, get the rectangle with the inverse given transform.
	 * The result rectangle is positive width and positive height.
	 * @param af AffineTransform
	 * @param src source rectangle
	 * @return rectangle after transform with positive width and height
	 */
	public static Rectangle inverseTransformRect(final AffineTransform af, Rectangle src)
	{
		final Rectangle dest= new Rectangle(0,0,0,0);
		src=absRect(src);
		Point p1=new Point(src.x,src.y);
		p1=inverseTransformPoint(af,p1);
		dest.x=p1.x; dest.y=p1.y;
		dest.width=(int)(src.width/af.getScaleX());
		dest.height=(int)(src.height/af.getScaleY());
		return dest;
	}

	/**
	 * Given an arbitrary point, get the point with the given transform.
	 * @param af affine transform
	 * @param pt point to be transformed
	 * @return point after tranform
	 */
	public static Point transformPoint(final AffineTransform af, final Point pt) 
	{
		final Point2D src = new Point2D.Float(pt.x, pt.y);
		final Point2D dest= af.transform(src, null);
		final Point point=new Point((int)Math.floor(dest.getX()), (int)Math.floor(dest.getY()));
		return point;
	}

	/**
	 * Given an arbitrary point, get the point with the inverse given transform.
	 * @param af AffineTransform
	 * @param pt source point
	 * @return point after transform
	 */
	public static Point inverseTransformPoint(final AffineTransform af, final Point pt)
	{
		final Point2D src=new Point2D.Float(pt.x,pt.y);
		try
		{
			final Point2D dest= af.inverseTransform(src, null);
			return new Point((int)Math.floor(dest.getX()), (int)Math.floor(dest.getY()));
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			return new Point(0,0);
		}
	}

	/**
	 * Given arbitrary rectangle, return a rectangle with upper-left 
	 * start and positive width and height.
	 * @param src source rectangle
	 * @return result rectangle with positive width and height
	 */
	public static Rectangle absRect(final Rectangle src)
	{
		final Rectangle dest = new Rectangle(0,0,0,0);
		if (src.width < 0) 
		{ 
			dest.x = src.x + src.width; 
			dest.width =- src.width; 
		} 
		else
		{ 
			dest.x = src.x; 
			dest.width = src.width; 
		}

		if (src.height < 0) 
		{ 
			dest.y = src.y + src.height;
			dest.height =- src.height; 
		} 
		else
		{ 
			dest.y = src.y;
			dest.height = src.height;
		}

		return dest;
	}

}