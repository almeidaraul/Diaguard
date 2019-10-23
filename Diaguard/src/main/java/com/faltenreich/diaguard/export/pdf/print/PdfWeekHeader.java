package com.faltenreich.diaguard.export.pdf.print;

import android.util.Log;

import com.faltenreich.diaguard.R;
import com.faltenreich.diaguard.export.pdf.PdfExportCache;
import com.pdfjet.Paragraph;
import com.pdfjet.Point;
import com.pdfjet.Text;
import com.pdfjet.TextLine;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

public class PdfWeekHeader implements PdfPrintable {

    private static final String TAG = PdfWeekHeader.class.getSimpleName();

    private static final float FONT_SIZE_HEADER = 15f;
    private static final float PADDING_PARAGRAPH = 20;
    private static final float PADDING_LINE = 3;

    private Text text;

    public PdfWeekHeader(PdfExportCache cache) {
        DateTime weekStart = cache.getDateTime().withDayOfWeek(1);
        TextLine week = new TextLine(cache.getFontBold());
        week.setFontSize(FONT_SIZE_HEADER);
        week.setText(String.format("%s %d",
            cache.getConfig().getContextReference().get().getString(R.string.calendarweek),
            weekStart.getWeekOfWeekyear())
        );
        Paragraph weekParagraph = new Paragraph(week);

        DateTime weekEnd = weekStart.withDayOfWeek(DateTimeConstants.SUNDAY);
        TextLine interval = new TextLine(cache.getFontNormal());
        interval.setText(String.format("%s - %s",
            DateTimeFormat.mediumDate().print(weekStart),
            DateTimeFormat.mediumDate().print(weekEnd))
        );
        Paragraph intervalParagraph = new Paragraph(interval);

        List<Paragraph> paragraphs = new ArrayList<>();
        paragraphs.add(weekParagraph);
        paragraphs.add(intervalParagraph);

        try {
            text = new Text(paragraphs);
            text.setParagraphLeading(week.getFont().getBodyHeight() + PADDING_LINE);
        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage());
        }
    }

    @Override
    public float getHeight() {
        if (text != null) {
            // Orientation is horizontal, so beginning points are top left and bottom left
            List<float[]> points = text.getBeginParagraphPoints();
            if (points.size() == 2) {
                float[] start = points.get(0);
                float[] end = points.get(1);
                if (start.length == 2 && end.length == 2) {
                    return end[1] - start[1];
                }
            }

        }
        return 0f;
    }

    @Override
    public Point drawOn(PdfPage page) throws Exception {
        Point position = page.getPosition();
        text.setLocation(position.getX(), position.getY());
        text.setWidth(page.getWidth());
        float[] points = text.drawOn(page);
        return new Point(position.getX(), points[1] + PADDING_PARAGRAPH);
    }
}
