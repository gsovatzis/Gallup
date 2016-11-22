package gr.gpshellas.Gallup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.soliant.android.view.widget.ToggleGroup;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * @version 1.1
 * @author George Sovatzis
 * Gallup Android application. This application will display a set of options, stored in an XML file
 * and store the values selected along with a date/time tag to a text file in the device's internal memory.
 *
 * (It would be nice if the text file could be a CSV file to be parsed by Excel :)
 * 
 */
public class Gallup extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // *** Get a reference to the maintable ***
    	final TableLayout mainTable = (TableLayout) findViewById(R.id.maintable);
    	        
        /* Before setting the layout we must read the options
         * in the gallup.xml file and create the TableLayout with the appropriate
         * togglebuttons.
         */
        try {
        	createOptions(mainTable);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        // *** Button listener on save ***
        final Button btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                	doSave(mainTable);	// *** Call the doSave method, to save button data on file and reset selections ***
                } catch(IOException e) {
                	e.printStackTrace();
                }
            }
        });
        
    }
    
    private void createOptions(TableLayout mainTable) throws XmlPullParserException, IOException {
    	
    	// *** Get the XML options file ***
    	Resources res = this.getResources();
    	XmlResourceParser xpp = res.getXml(R.xml.gallup);
    	
    	TableRow trHeader = null;	// *** An empty header row ***
    	TableRow trControls = null;	// *** And empty controls row ***
    	ToggleGroup rGroup = null;	// *** An empty toggle group ***
    	
    	// *** Iterate through the XML options file to build the TableLayout ***
    	xpp.next();
    	int eventType = xpp.getEventType();
    	int labelId = 0;		// *** Sequential ID for the table rows ***
    	int buttonId = 0; 	// *** Sequential ID for the radio buttons ***
    	
    	// *** One header row will be added ***
    	trHeader = new TableRow(this);
		trHeader.setId(100);
		trHeader.setLayoutParams(new LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
		
		trControls = new TableRow(this);
		trControls.setId(101);
		trControls.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		    	
    	while (eventType != XmlPullParser.END_DOCUMENT)
    	{
    		if(eventType == XmlPullParser.START_TAG) {
    			if(xpp.getName().equals("gallup_category")) {
    				// *** We have a new category! Add a TextView label to the header row ***
    				
    				TextView lbl_option = new TextView(this);
    				lbl_option.setId(200 + labelId);
    	            lbl_option.setText(xpp.getAttributeValue(0));
    	            lbl_option.setTextColor(Color.WHITE);
    	            trHeader.addView(lbl_option);
    	            
    	            rGroup = new ToggleGroup(this);
    	            rGroup.setOrientation(1);	// *** Vertical Orientation ***
    	            trControls.addView(rGroup);
    	            
    				labelId +=1;	// *** Advance the trId ***
    			}
    		}
    		
    		if(eventType == XmlPullParser.TEXT) {
				// *** We have a new gallup option. Add a ToggleButton to the ToggleGroup ***
				ToggleButton rButton = new ToggleButton(this);
				rButton.setId(1000 + buttonId);
				rButton.setTextColor(Color.BLACK);
				rButton.setText(xpp.getText());
				rButton.setTextOff(xpp.getText());
				rButton.setTextOn(xpp.getText());
				rButton.setWidth(80);
				rButton.setHeight(80);
				rGroup.addView(rButton,0,new ToggleGroup.LayoutParams(
						ToggleGroup.LayoutParams.WRAP_CONTENT,
						ToggleGroup.LayoutParams.WRAP_CONTENT));
				    				
				buttonId +=1;	// *** Advance the buttonId;
			}
   				    		
    		eventType = xpp.next();	 // *** Go to the next XML element ***
    	
    	} 

    	// *** Add the header row to the table ***
    	mainTable.addView(trHeader, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
    	
    	// *** Add the controls row to the table ***
    	mainTable.addView(trControls, new TableLayout.LayoutParams(
    			LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
		
    }
    
    private void doSave(TableLayout mainTable) throws IOException {
    	/*
    	 * This method should iterate through maintable's rows and get each ToggleGroup value from the row.
    	 * It then should save the records to a file
    	 */
    	
    	StringBuffer myRecord = new StringBuffer();
    	StringBuffer myHeader = new StringBuffer();
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	String mySavedDate = getString(R.string.mysavedate);
    	
    	myHeader.append(mySavedDate + ";");
    	myRecord.append(sdf.format(new Date()) + ";");
    	
    	    	
    	// *** Iterate through table rows ***
    	for(int i=0;i<mainTable.getChildCount();i++) {
    		if(mainTable.getChildAt(i) instanceof TableRow) {
    			TableRow tr = (TableRow)mainTable.getChildAt(i);
    			
    			// *** Iterate through each table row Items ***
    			for(int trItem=0;trItem<tr.getChildCount();trItem++) {
    				
    				if(tr.getChildAt(trItem) instanceof TextView) {
    					myHeader.append(((TextView)tr.getChildAt(trItem)).getText() + ";");
    					
    				}
    				
    				if(tr.getChildAt(trItem) instanceof ToggleGroup) {
    					ToggleGroup rGroup = (ToggleGroup) tr.getChildAt(trItem);
    					String selectedValue;
    					if(rGroup.getCheckedRadioButtonId()==-1) {
    						selectedValue = "";
    					} else {
        					ToggleButton selBtn = (ToggleButton)findViewById(rGroup.getCheckedRadioButtonId());
    						selectedValue = selBtn.getText().toString();
    					}
    					myRecord.append(selectedValue + ";");
    					rGroup.clearCheck();	// *** Clear the selection ***
    				}
    			}
    			
    		}
    	}
    	
    	/*
    	 *  Do the actual save: We must check the following:
    	 *  1. Check if file already exists in folder. If not, create file with headers...
    	 *  2. If file exists, open and append existing records
    	 *  3. Close file  
    	 */
    	
    	String myFileName="gallup.txt";
    	
    	File extPath = Environment.getExternalStorageDirectory();
    	File fullPath = new File(extPath.getAbsolutePath() + "/" + myFileName);
    	
    	String state = Environment.getExternalStorageState();
    	if(Environment.MEDIA_MOUNTED.equals(state)) {
    		// *** If we can read and write, continue ***
    		
    		if(!fullPath.exists()){
    			// *** If file doesn't exist, create a new file and insert header and record ***
    			FileOutputStream fos = new FileOutputStream(fullPath.getAbsolutePath(), false);
        		fos.write(myHeader.toString().getBytes());fos.write("\r\n".getBytes());
            	fos.write(myRecord.toString().getBytes());fos.write("\r\n".getBytes());
            	fos.close();
            	
    		} else {
    			// *** If the file exists, append the record ***
    			FileOutputStream fos = new FileOutputStream(fullPath.getAbsolutePath(), true);
    			fos.write(myRecord.toString().getBytes());fos.write("\r\n".getBytes());
            	fos.close();
            	
    		}
    		    		
        	Toast.makeText(this, R.string.rec_saved, Toast.LENGTH_LONG).show();
    		
    	} else {
    		// *** else show message ***
    		Toast.makeText(this, R.string.cannot_save, Toast.LENGTH_LONG).show();
    	}
    	    	    	
    }
}