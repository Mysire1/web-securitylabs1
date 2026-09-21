package chnu.edu.websecuritylabs.item;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ItemService {

    private final Map<String, Item> items = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        items.put("1", new Item("1", "name1", "description1"));
        items.put("2", new Item("2", "name2", "description2"));
        items.put("3", new Item("3", "name3", "description3"));
    }

    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    public Item getById(String id) {
        return items.get(id);
    }

    public Item create(Item item) {
        if (item.getId() == null || item.getId().isBlank()) {
            item.setId(UUID.randomUUID().toString());
        }
        items.put(item.getId(), item);
        return item;
    }

    public Item update(Item item) {
        if (item.getId() == null || !items.containsKey(item.getId())) {
            return null;
        }
        items.put(item.getId(), item);
        return item;
    }

    public void deleteById(String id) {
        items.remove(id);
    }
}
