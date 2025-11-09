package Models;

public class panier {

    private int id;
    private Integer UserId;

    public panier() {}

    public panier(Integer UserId) {
        this.UserId = UserId;
    }
    public panier(int id, Integer UserId) {
        this.id = id;
        this.UserId = UserId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return UserId;
    }

    public void setUserId(Integer userId) {
        UserId = userId;
    }
    @Override
    public String toString() {
        return "panier{" +
                "id=" + id +
                ", UserId=" + UserId +
                '}';
    }
}
