package ftpupload;

import java.awt.Component;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * 
 * @author BruceXX
   单元格渲染器,用来将单元格内的东东转换为其它显示内容
 */

public class UploadTableCellRender implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub
		JLabel label=null;
		if(value instanceof Icon){
			label=new JLabel((Icon)value);
		}else if(value instanceof String){
			label=new JLabel(value.toString());
			label.setToolTipText(value.toString());
		}else if(value instanceof File){
			
			File file=(File)value;
			label=new JLabel(file.getName());
			label.setToolTipText(file.getPath());
		}
		
		return label;
	}

}
