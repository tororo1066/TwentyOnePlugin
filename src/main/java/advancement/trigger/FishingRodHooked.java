package advancement.trigger;

import advancement.data.EntityData;
import advancement.data.ItemData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.function.Consumer;

public class FishingRodHooked implements Trigger {

    @Expose
    @SerializedName("entity")
    private EntityData entity;

    @Expose
    @SerializedName("item")
    private ItemData item;

    @Expose
    @SerializedName("rod")
    private ItemData rod;

    public void setEntity(Consumer<EntityData> consumer) {
        this.entity = new EntityData();
        consumer.accept(entity);
    }

    public void setItem(Consumer<ItemData> consumer) {
        this.item = new ItemData();
        consumer.accept(item);
    }

    public void setRod(Consumer<ItemData> consumer) {
        this.rod = new ItemData();
        consumer.accept(rod);
    }
}
