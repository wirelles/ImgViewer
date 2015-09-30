package imageviewer;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;


public class TestImageViewer 
{
	public static void main (String [] args) 
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));

		// Select file to open
		final FileDialog fd = new FileDialog(shell, SWT.OPEN|SWT.MULTI);
	
		final String[] filterExt = { "*.*" };
		fd.setFilterExtensions(filterExt);
//		final String filepath = fd.getFileName();
		Collection<Image> images = new ArrayList<>();
//		images = Image[10]
		Image image = null;
		if( fd.open()!= null)
		for(String filepath: fd.getFileNames())
		{
			System.out.println(filepath);
			
			image = new Image(Display.getDefault(), "E:/Poze/pt ipod/" + filepath);
			
			images.add(image);
		}
		MultiImageViewerComponent  awtControlDemo = new MultiImageViewerComponent()
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true))
				.build(shell);
		awtControlDemo.show(images);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		for(Image img: images)
		{
			img.dispose();
		}
		display.dispose();
		
		
	}

}