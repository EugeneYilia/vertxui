package live.connector.vertxui.samples.client.mvcBootstrap.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Bills {

	public List<Bill> all;

	public Bills() {
		all = new ArrayList<>();
	}

	public static class Bill implements Comparable<Bill> {

		public int id;
		public Name who;
		public double amount;
		public String what;
		public Date date;

		public Bill() { // empty constructor for serialization
		}

		public Bill(Name who, double amount, String what, Date date) {
			this.who = who;
			this.amount = amount;
			this.what = what;
			this.date = date;
		}

		@Override
		public int compareTo(Bill o) {
			return o.date.compareTo(date);
		}

	}

	public enum Name {
		Linda, Niels
	}

}
