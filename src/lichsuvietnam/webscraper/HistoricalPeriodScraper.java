package lichsuvietnam.webscraper;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import lichsuvietnam.models.HistoricalPeriod;

public class HistoricalPeriodScraper {
	public void getData(String url, String output) {
		Document document = null;
		try {
			document = Jsoup.connect(url).userAgent("Jsoup client").timeout(20000).get();

			Elements lstPeriods = document.select(
					".table > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr > td:nth-child(1)");

			ArrayList<HistoricalPeriod> historicalPeriods = new ArrayList<>();
			for (Element element : lstPeriods) {
				HistoricalPeriod historicalPeriod = new HistoricalPeriod();
				historicalPeriod.setName(element.child(0).text());
				List<Element> elements = element.getElementsByTag("font");
				if (elements.size() > 0) {
					String timespan = elements.get(0).text();
					timespan = timespan.substring(1, timespan.length() - 1);
					historicalPeriod.setTimespan(timespan);
				} else {
					historicalPeriod.setTimespan("");
				}
				historicalPeriods.add(historicalPeriod);
			}
			
			historicalPeriods.remove(historicalPeriods.size() - 1);
			historicalPeriods.remove(historicalPeriods.size() - 1);

			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

			writer.writeValue(Paths.get(output).toFile(), historicalPeriods);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
