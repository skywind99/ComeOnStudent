package kr.hs.namyangju.comeonstudent;

public class ListViewItem {
    private String dateStr;
    private String countStr;
    private String nameStr;

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getCountStr() {
        return countStr;
    }

    public void setCountStr(String countStr) {
        this.countStr = countStr;
    }

    public String getNameStr() {
        return nameStr;
    }

    public void setNameStr(String nameStr) {
        this.nameStr = nameStr;
    }

    public ListViewItem(String dateStr, String countStr, String nameStr) {
        this.dateStr = dateStr;
        this.countStr = countStr;
        this.nameStr = nameStr;
    }
}
