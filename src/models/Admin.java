package models;

final public class Admin extends Organizer {
    public Admin(String name, String password)
    {
        super(name,password);
    }
    @Override
    public void showPanel()
    {
        super.showPanel();
    }
    @Override
    public boolean checkCommand(Command command)
    {
        if (command==null)
            return false;
        if(command.getValue()>=0 && command.getValue()<=9)
            return true;
        return false;
    }
}
