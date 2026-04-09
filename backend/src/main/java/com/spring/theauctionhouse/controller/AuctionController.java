package com.spring.theauctionhouse.controller;

import com.spring.theauctionhouse.entity.AuctionItem;
import com.spring.theauctionhouse.entity.User;
import com.spring.theauctionhouse.repository.AuctionRepository;
import com.spring.theauctionhouse.service.AuctionService;
import com.spring.theauctionhouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuctionController {

    private final AuctionService auctionService;
    private final UserService userService;
    private final AuctionRepository auctionRepository;

    @PostMapping("/create")
    public ResponseEntity<AuctionItem> create(@RequestBody AuctionItem item, Principal principal) {
        return ResponseEntity.ok(auctionService.createAuction(item, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuctionItem> update(@PathVariable Long id, @RequestBody AuctionItem item, Principal principal) {
        return ResponseEntity.ok(auctionService.updateAuction(id, item, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, Principal principal) {
        auctionService.deleteAuction(id, principal.getName());
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/search")
    public ResponseEntity<List<AuctionItem>> search(@RequestParam(required = false) String keyword, @RequestParam(required = false) String category) {
        return ResponseEntity.ok(auctionService.globalSearch(keyword, category));
    }

    @GetMapping("/filter/price")
    public ResponseEntity<List<AuctionItem>> filterPrice(@RequestParam Double min, @RequestParam Double max) {
        return ResponseEntity.ok(auctionService.getByPriceRange(min, max));
    }

    @GetMapping("/filter/shipping")
    public ResponseEntity<List<AuctionItem>> filterShipping(@RequestParam String option) {
        return ResponseEntity.ok(auctionService.getByShipping(option));
    }

    @GetMapping("/reports/ongoing")
    public ResponseEntity<List<AuctionItem>> myOngoing(Principal principal) {
        return ResponseEntity.ok(auctionService.getMyOngoing(principal.getName()));
    }

    @GetMapping("/reports/history")
    public ResponseEntity<List<AuctionItem>> myHistory(Principal principal) {
        return ResponseEntity.ok(auctionService.getMyHistory(principal.getName()));
    }

    @GetMapping("/reports/my-auctions")
    public List<AuctionItem> getMyAuctions(Principal principal) {
        User currentUser = userService.getProfile(principal.getName());
        return auctionRepository.findByOwner(currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionItem> getById(@PathVariable Long id) { return ResponseEntity.ok(auctionService.getAuctionById(id)); }

    @GetMapping
    public ResponseEntity<List<AuctionItem>> getAll() { return ResponseEntity.ok(auctionService.getAll()); }
}