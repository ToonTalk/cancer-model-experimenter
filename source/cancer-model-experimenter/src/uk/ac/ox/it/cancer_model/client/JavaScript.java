package uk.ac.ox.it.cancer_model.client;

public class JavaScript {

	public static native double sliderValue(String sliderId) /*-{
	  return parseFloat($wnd.$("#" + sliderId).val());
	}-*/;
	
}
