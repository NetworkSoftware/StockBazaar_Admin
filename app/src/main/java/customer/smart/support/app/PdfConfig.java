package customer.smart.support.app;

import static customer.smart.support.app.PdfConfigInvoice.createTextRight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import customer.smart.support.R;
import customer.smart.support.address.AddressBean;


public class PdfConfig {

    private static final BaseColor whiteBase = new BaseColor(255, 255, 255);
    private static final BaseColor greenBase = new BaseColor(14, 157, 31);
    private static BaseFont urName;
    private static final Font catNormalFont = new Font(urName, 13, Font.BOLD);
    private static final Font catNormalFontTitel = new Font(urName, 15, Font.BOLD);
    private static Font invoiceFont = new Font(urName, 20, Font.BOLD, greenBase);

    {
        try {
            urName = BaseFont.createFont("font/sans.TTF", "UTF-8", BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addMetaData(Document document) {
        document.addTitle("My first PDF");
        document.addSubject("Using iText");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("Lars Vogel");
        document.addCreator("Lars Vogel");
    }

    public static void addContent(String orderId, Document document, AddressBean addressBean, Context context) throws Exception {

        invoiceFont = new Font(urName, 20, Font.BOLD, greenBase);


        PdfPTable table1 = new PdfPTable(1);
        table1.setWidthPercentage(100);
        table1.setWidths(new float[]{1});

        PdfPTable table01 = new PdfPTable(3);
        table01.setWidthPercentage(100);
        table01.setWidths(new int[]{1, 1, 1});
        table01.addCell(createTextCenterWithLeft("" + "\n", catNormalFontTitel));
        table01.addCell(createTextLeft("FROM" + "\n", catNormalFontTitel));
        if (addressBean.getSelleraddress().toLowerCase(Locale.ROOT).contains("stock bazaar")) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sb_pdf);
            icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Image img = Image.getInstance(byteArray);
            img.scaleAbsolute(100, 100);
            table01.addCell(createTextImage(img));
        } else {
            table01.addCell(createTextImage(null));
        }

        table01.addCell(createTextRight("", catNormalFont));
        table1.addCell(createTable(table01, 0, whiteBase, false));
        table1.addCell(createTextCenter("\t\t" + addressBean.getSelleraddress(), catNormalFont));
        table1.addCell(createTextCenter("\n", catNormalFont));
        table1.addCell(createTextBorder("TO" + "\n", catNormalFontTitel));
        table1.addCell(createTextCenter("\t\t" + addressBean.getBuyeraddress(), catNormalFont));
     //   table1.addCell(createTextCenter("\n", catNormalFont));

        //  table1.addCell(createTextCellTable("", catNormalFont));

        PdfPTable table11 = null;
        if (addressBean.getCod() != null && addressBean.getCod().length() > 0) {
            table11 = new PdfPTable(2);
            table11.setWidthPercentage(100);
            table11.setWidths(new float[]{1, 1});
            table11.addCell(createTextCenterWithRight("ID : " + addressBean.getIdtext(), catNormalFont));
            table11.addCell(createTextCenterNo("COD Amt : ₹" + addressBean.getCod(), catNormalFont));
        } else {
            table11 = new PdfPTable(1);
            table11.setWidthPercentage(100);
            table11.setWidths(new float[]{1});
            table11.addCell(createTextCenterNo("ID : " + addressBean.getIdtext(), catNormalFont));
        }

        if ("NA".equalsIgnoreCase(orderId)) {

        } else {
            PdfPTable table111 = null;
            table111 = new PdfPTable(1);
            table111.setWidthPercentage(100);
            table111.setWidths(new float[]{1});
            table111.addCell(createTextCenterNo("ORDER ID : #" + orderId, catNormalFont));
            table1.addCell(table111);
        }


        table1.addCell(table11);
        table1.setKeepTogether(true);
        document.add(table1);
    }


    public static PdfPCell createTable(PdfPTable pTable, int padding, BaseColor baseColor, boolean isBorder) throws DocumentException, IOException {

        PdfPCell cell = new PdfPCell();
        cell.addElement(pTable);
        cell.setPaddingTop(0);
        cell.setPaddingBottom(0);
        cell.setPaddingLeft(padding);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(baseColor);
        return cell;
    }

    public static PdfPCell createTextCellTable(String text, Font font) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
        return cell;
    }

    public static PdfPCell createTextCenter(String text, Font font) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        return cell;
    }

    public static PdfPCell createTextCenterWithLeft(String text, Font font) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.LEFT | Rectangle.TOP);
        return cell;
    }

    public static PdfPCell createTextLeft(String text, Font font) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.TOP);
        return cell;
    }

    public static PdfPCell createTextImage(Image image) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        cell.addElement(image);
        cell.setUseAscender(true);
        cell.setPaddingLeft(10);
        cell.setBorder(Rectangle.TOP | Rectangle.RIGHT);
        return cell;
    }

    public static PdfPCell createTextCenterWithRight(String text, Font font) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        cell.setPadding(3);
        cell.setPaddingBottom(10);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.RIGHT);
        return cell;
    }

    public static PdfPCell createTextCenterNo(String text, Font font) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        cell.setPaddingBottom(2);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    public static PdfPCell createTextBorder(String text, Font font) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        cell.setPadding(0);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        return cell;
    }

}
