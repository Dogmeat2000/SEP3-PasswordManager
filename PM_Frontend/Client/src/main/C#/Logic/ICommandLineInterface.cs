namespace PM_Frontend.C#.Logic
{
    public interface ICommandLineInterface
    {
        void Start();
        void HandleCommand(string command);
    }
}