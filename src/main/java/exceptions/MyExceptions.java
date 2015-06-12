package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class MyExceptions {
@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Invalid Parameters or missing parameters")
	public static class InvalidParameterException extends RuntimeException{
		public InvalidParameterException(){
			
		}
	}
@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Id not found")
public static class IdIsNotFoundException extends RuntimeException{
	public IdIsNotFoundException(){
		System.out.println("Id not found");
	}
}
}
