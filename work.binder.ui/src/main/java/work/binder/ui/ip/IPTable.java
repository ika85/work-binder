package work.binder.ui.ip;

import java.util.Properties;

import work.binder.ui.LayoutReloadComponent;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class IPTable extends LayoutReloadComponent {

    private Table _table;

    // TODO should we add Remove Button for IP; and/or Edit button for the
    // comment
    public IPTable(Properties properties) {
	Table table = new Table("IPs");
	setTable(table);
	addComponent(table);

	table.setWidth("100%");
	table.setHeight("170px");

	table.setSelectable(true);
	table.setMultiSelect(true);
	table.setImmediate(true);

	table.setContainerDataSource(IPAddressTablePreparator
		.provideIPAddressContainer(properties));

	table.setColumnReorderingAllowed(true);
	table.setColumnCollapsingAllowed(true);

	table.setColumnHeaders(new String[] { "IP Address", "Available",
		"Busy", "Comment" });

	table.setColumnAlignment(IPAddressTablePreparator.IP_ADDRESS,
		Table.ALIGN_CENTER);
	table.setColumnAlignment(IPAddressTablePreparator.AVAILABLE,
		Table.ALIGN_CENTER);
	table.setColumnAlignment(IPAddressTablePreparator.BUSY,
		Table.ALIGN_CENTER);
	table.setColumnAlignment(IPAddressTablePreparator.COMMENT,
		Table.ALIGN_CENTER);

	table.setColumnWidth(IPAddressTablePreparator.IP_ADDRESS, 90);
	table.setColumnWidth(IPAddressTablePreparator.AVAILABLE, 70);
	table.setColumnWidth(IPAddressTablePreparator.BUSY, 70);
	table.setColumnWidth(IPAddressTablePreparator.COMMENT, 90);

    }

    public Table getTable() {
	return _table;
    }

    @Override
    public void reload() {

	IPAddressTablePreparator.reloadIPAddressContainer(getTable()
		.getContainerDataSource());
    }

    private void setTable(Table table) {
	_table = table;
    }
}
