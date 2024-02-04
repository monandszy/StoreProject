package code.domain.exception;

public class ObjectIdNotAllowedException extends RuntimeException{
   public ObjectIdNotAllowedException() {
      super("Adding object with id present might result in duplicates, please use update instead");
   }
}