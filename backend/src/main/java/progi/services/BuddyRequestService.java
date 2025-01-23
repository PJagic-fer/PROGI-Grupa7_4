package progi.services;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import progi.data.ApplicationUser;
import progi.data.BuddyRequest;
import progi.repositories.BuddyRequestRepository;

@Service
public class BuddyRequestService {

    private final BuddyRequestRepository buddyRequestRepository;

    @Autowired
    public BuddyRequestService(BuddyRequestRepository buddyRequestRepository) {
        this.buddyRequestRepository = buddyRequestRepository;
    }

    public void addNewBuddyRequest(BuddyRequest request) {
        BuddyRequest foundBuddyRequest = buddyRequestRepository.findByUser(request.getUser());
        if (foundBuddyRequest == null || foundBuddyRequest.getIsBlocked()) {
            buddyRequestRepository.save(request);
            return;
        }
        foundBuddyRequest.setBuddy(request.getBuddy());
        foundBuddyRequest.setDateCreated(LocalDateTime.now());
        foundBuddyRequest.setHasBuddyAccepted(false);

        buddyRequestRepository.save(foundBuddyRequest);
    }

    public String getAllBuddyRequests()
    {
        List<BuddyRequest> requests = buddyRequestRepository.findAll();
        
        return requests.toString();
    }

    public Pair<BuddyRequest, Boolean> getBuddyRequest(ApplicationUser from, ApplicationUser to) {
        List<BuddyRequest> requests = buddyRequestRepository.findAll();

        Pair<BuddyRequest, Boolean> ret = Pair.of(null, false); // request nije naden, vrati prazno

        for(BuddyRequest request : requests){
            if(request.getUser().equals(from) && request.getBuddy().equals(to))
            {
                ret = Pair.of(request, true); // nasli request, vracamo ga
            }
        }

        return ret;
    }

    public Boolean editBuddyStatus(Long studentId, Long buddyId, Boolean isBuddy, ApplicationUserService applicationUserService)
    {
        List<BuddyRequest> requests = getAllRequestsForId(studentId);
        if (requests.size() == 0)
        {
            return false;
        }
        for(BuddyRequest request : requests)
        {
            if(request.getBuddy().getId() == buddyId)
            {
                ApplicationUser user = applicationUserService.getUserById(studentId).orElse(null);
                if(!isBuddy)
                {
                    request.setIsBlocked(false);
                    if(user != null)
                    {
                        user.setBuddy(null);
                        applicationUserService.updateApplicationUser(user);
                    }
                }
                else
                {
                    if(user != null)
                    {
                        ApplicationUser buddy = applicationUserService.getUserById(buddyId).orElse(null);
                        user.setBuddy(buddy);
                        applicationUserService.updateApplicationUser(user);
                    }
                }
                request.setHasBuddyAccepted(isBuddy);
                buddyRequestRepository.save(request);
                return true;
            }
        }
        return false;
    }

    public List<BuddyRequest> getAllRequestsForId(Long userID)
    {
        List<BuddyRequest> requests = buddyRequestRepository.findAll();

        List<BuddyRequest> ret = new ArrayList<>();

        for(BuddyRequest request : requests){
            if(request.getBuddy().getId() == userID)
            {
                ret.add(request);
            }
        }

        return ret;
    }

    public ApplicationUser getRequestedBuddyByUser(ApplicationUser user) {
        BuddyRequest buddyRequest = buddyRequestRepository.findByUser(user);
        if (buddyRequest != null) {
            return buddyRequest.getBuddy();
        }
        return null;
    }
}
