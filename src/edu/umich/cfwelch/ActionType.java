package edu.umich.cfwelch;

public class ActionType {
	
	private String type;
	private Response response;
	
	public ActionType(String type, Response response) {
		this.type = type;
		this.response = response;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}

}
