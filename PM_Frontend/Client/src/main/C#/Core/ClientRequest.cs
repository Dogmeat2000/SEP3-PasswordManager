namespace PM_Frontend.Core
{
    
    /* Instead of sending the MasterUserDTO, maybe we should send ClientRequest,
     which tells backend what kind of request it is? Maybe we already went over this */
    public class ClientRequest
    {
        public string RequestType { get; set; }
        public LoginEntryDTO Dto { get; set; }

        public ClientRequest(string requestType, LoginEntryDTO dto)
        {
            RequestType = requestType;
            Dto = dto;
        }
    }
}