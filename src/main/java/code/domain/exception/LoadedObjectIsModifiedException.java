package code.domain.exception;

public class LoadedObjectIsModifiedException extends RuntimeException {
   public LoadedObjectIsModifiedException() {
      super("This object is already loaded and has been modified, update database before fetching");
   }
}