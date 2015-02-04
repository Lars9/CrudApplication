package ui;

import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import dao.UserDao;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import table.User;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import java.util.logging.Filter;

/**
 * Created by Александр on 27.10.14.
 */
@Theme("valo")
public class CrudApplicationUserInterface extends UI  {


    private static final String fieldsName[] = {"name", "age"};
    private FormLayout editor = new FormLayout();
    private FieldGroup editorFields = new FieldGroup();
    private static final String fields[] = {"id", "name", "age", "isAdmin"};
    private FormLayout ed = new FormLayout();
    private FieldGroup edFields = new FieldGroup();
    private Button addButtonUser = new Button("Add");
    private Button updateButton = new Button("Update User");
    private Button removeButton = new Button("Delete User");
    private PagedTable table = createTable();
    private TextField searchField = new TextField();
    private ComboBox box = new ComboBox("isAdmin");
    private ComboBox setIsAdminBox = new ComboBox("isAdmin");

    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"}, true);
    UserDao userDao = (UserDao)context.getBean("userDao");

    Panel panel = new Panel("Add new user");
    Panel update = new Panel("Update or delete user");

    //HorizontalLayout tableControls;



    public PagedTable createTable()
    {
        PagedTable table = new PagedTable();
        table.setPageLength(10);
        table.setVisible(true);
        table.setImmediate(true);
        table.setSelectable(true);

        return table;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        initLayout();
        initTable();
        initEditor();
        initButton();
        initSearch();
    }

    public void initLayout()
    {
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        setContent(splitPanel);
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout rightLayout = new VerticalLayout();
        splitPanel.addComponent(verticalLayout);
        //verticalLayout.setSizeFull();
        splitPanel.addComponent(rightLayout);
        splitPanel.setSplitPosition(65, Sizeable.UNITS_PERCENTAGE);
        verticalLayout.addComponent(table);
        HorizontalLayout tableControls = table.createControls();
        ((HorizontalLayout) tableControls.getComponent(0)).getComponent(1).setWidth("90px");
        ((HorizontalLayout) tableControls.getComponent(1)).getComponent(3).setWidth("30px");
        ((TextField)((HorizontalLayout) tableControls.getComponent(1)).getComponent(3)).removeAllValidators();
        verticalLayout.addComponent(tableControls);
        HorizontalLayout bottom = new HorizontalLayout();
        verticalLayout.addComponent(bottom);
        bottom.addComponent(searchField);
        bottom.setWidth("300px");
        bottom.setHeight("100px");
        bottom.setComponentAlignment(searchField, Alignment.MIDDLE_CENTER);
        searchField.setInputPrompt("Search");
        searchField.setWidth("300");

        rightLayout.addComponent(panel);
        panel.setVisible(true);

        panel.setWidth("600px");
        panel.setHeight("250px");
        rightLayout.addComponent(update);
        update.setVisible(false);
        update.setWidth("600px");
        update.setHeight("350px");
    }

    public void initTable()
    {
        table.setWidth("995px");
        table.addContainerProperty("id", Integer.class, null);
        table.addContainerProperty("name", String.class, null);
        table.addContainerProperty("age", Integer.class, null);
        table.addContainerProperty("isAdmin", Boolean.class, null);
        table.addContainerProperty("createDate", Date.class, null);


        List<User> users = userDao.getAllUsers();

        for (User item : users)
        {
            table.addItem(new Object[]{item.getId(),item.getName(), item.getAge(), item.isAdmin(), item.getCreateDate().getTime()}, item.getId());
        }

        table.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {


                Object contactId = table.getValue();
                if (contactId != null)
                    edFields.setItemDataSource(table.getItem(contactId));
                edFields.getField(fields[0]).setEnabled(false);
                setIsAdminBox.setValue(edFields.getField(fields[3]).getValue());
                edFields.getField(fields[3]).setVisible(false);
                update.setVisible(contactId != null);
            }
        });

        //



        table.createControls();
       // tableControls = Controls;
        //table.createControls().setWidth("200px");
        //table.createControls().setSizeUndefined();

        table.setPageLength(10);
    }

    public void initSearch()
    {
        searchField.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.LAZY);

        searchField.addTextChangeListener(new FieldEvents.TextChangeListener() {
          @Override
          public void textChange(final FieldEvents.TextChangeEvent textChangeEvent) {

              List<User> users = userDao.getAllUsers();
             table.removeAllItems();

              if (!textChangeEvent.getText().isEmpty())
              {
                  table.removeAllItems();
                  table.createControls().removeAllComponents();
                  for (User user : users)
                  {

                      if (user.getName().toLowerCase().contains(textChangeEvent.getText().toLowerCase()/*searchField.getValue()*/))
                      {


                          table.addItem(new Object[]{user.getId(),user.getName(), user.getAge(), user.isAdmin(), user.getCreateDate().getTime()}, user.getId());
                      }
                  }

                  table.createControls();
                  table.setPageLength(10);
              }
              else
              {
                  table.removeAllItems();
                  table.createControls().removeAllComponents();


                  for (User item : users)
                  {
                      table.addItem(new Object[]{item.getId(),item.getName(), item.getAge(), item.isAdmin(), item.getCreateDate().getTime()}, item.getId());
                  }

                  table.createControls();
                  table.setPageLength(10);
              }

          }
      });
    }

    public void initEditor()
    {


        for (String item : fieldsName)
        {
            TextField field = new TextField(item);
            editor.addComponent(field);
            field.setWidth("70%");

            editorFields.bind(field, item);
        }

        editor.addComponent(box);
        editorFields.bind(box, "isAdmin");
        box.addItems("true", "false");
        box.setValue("false");
        editor.setMargin(true);
        editor.addComponent(addButtonUser);
        panel.setContent(editor);



        for (String item : fields)
        {
            TextField field = new TextField(item);
            ed.addComponent(field);
            field.setWidth("40%");

            edFields.bind(field, item);
        }

        ed.addComponent(setIsAdminBox);
        setIsAdminBox.addItems("false", "true");
        edFields.getField(fields[0]).setReadOnly(true);
        ed.setMargin(true);
        HorizontalLayout layout = new HorizontalLayout();
        ed.addComponent(layout);
        layout.addComponent(updateButton);
        layout.addComponent(removeButton);
        update.setContent(ed);




    }

    public void initButton()
    {


        addButtonUser.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                editorFields.getField(fieldsName[0]).removeAllValidators();
                editorFields.getField(fieldsName[1]).removeAllValidators();

                boolean f1 = true;
                boolean f2 = true;
                boolean f3 = true;
                boolean f4 = true;

                if (((String) editorFields.getField(fieldsName[0]).getValue()).isEmpty())
                {
                    f2 = false;
                    editorFields.getField(fieldsName[0]).addValidator(new NullValidator("field \"name\" can't be empty",true));

                }
                else
                {
                    Pattern pattern = Pattern.compile("^[A-Za-z]{1,}.{0,}");
                    Matcher matcher = pattern.matcher(((String) editorFields.getField(fieldsName[0]).getValue()));
                    if (!matcher.matches())
                    {
                        f2 = false;
                        editorFields.getField(fieldsName[0]).addValidator(new RegexpValidator("^[A-Za-z]{1,}.{0,}", "Name can't start with digit"));
                    }
                }

                if ( ((String) editorFields.getField(fieldsName[1]).getValue()).isEmpty())
                {
                    f3 = false;
                    editorFields.getField(fieldsName[1]).addValidator(new NullValidator("field \"age\" can't be empty",true));
                }

                else
                {
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(((String) editorFields.getField(fieldsName[1]).getValue()));

                    if (!matcher.matches())
                    {
                        f4 =false;
                        editorFields.getField(fieldsName[1]).addValidator(new RegexpValidator("\\d+","Field \"age\" don't consist symbols"));
                    }
                    else
                    {
                        if (Integer.parseInt((String)editorFields.getField(fieldsName[1]).getValue())>200 || Integer.parseInt((String)editorFields.getField(fieldsName[1]).getValue())<0)
                        {
                            f1 = false;
                            editorFields.getField(fieldsName[1]).addValidator(new IntegerRangeValidator("Age should be > 0 and < 200", 0, 200));
                        }
                    }
                }

                if (f1 && f2 && f3 && f4)
                {


                    User user = new User();
                    user.setName((String)editorFields.getField(fieldsName[0]).getValue());
                    String age = (String)editorFields.getField(fieldsName[1]).getValue();
                    user.setAge(Integer.parseInt(age));
                    String isAdmin = (String) box.getValue();
                    user.setAdmin(Boolean.parseBoolean(isAdmin));
                    user.setCreateDate(new GregorianCalendar());
                    userDao.addUser(user);
                    table.addItem(new Object[]{user.getId(), user.getName(), user.getAge(), user.isAdmin(), user.getCreateDate().getTime()}, user.getId());

                    for (String item : fieldsName)
                    {
                        ((TextField)editorFields.getField(item)).setValue("");

                    }

                }
            }
        });

        updateButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {


                edFields.getField(fields[0]).removeAllValidators();
                edFields.getField(fields[1]).removeAllValidators();
                edFields.getField(fields[2]).removeAllValidators();

                boolean f1 = true;
                boolean f2 = true;
                boolean f3 = true;
                boolean f4 = true;

                if (((String)edFields.getField(fields[0]).getValue()).isEmpty())
                {

                }

                if (((String) edFields.getField(fields[1]).getValue()).isEmpty())
                {
                    f2 = false;
                    edFields.getField(fields[1]).addValidator(new NullValidator("Field \"name\" can't be empty",true));

                }
                else
                {
                    Pattern pattern = Pattern.compile("^[A-Za-z]{1,}.{0,}");
                    Matcher matcher = pattern.matcher(((String) edFields.getField(fields[1]).getValue()));
                    if (!matcher.matches())
                    {
                        f2 = false;
                        edFields.getField(fields[1]).addValidator(new RegexpValidator("^[A-Za-z]{1,}.{0,}", "Name can't start with digit"));
                    }

                }


                if ( (String)edFields.getField(fields[2]).getValue() == null)
                {

                    f3 = false;
                    ((TextField)edFields.getField(fields[2])).setValue("1");
                    edFields.getField(fields[2]).addValidator(new NullValidator("field \"age\" can't be empty", true));
                }

                else
                {

                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(((String) edFields.getField(fields[2]).getValue()));
                    String regexp = "\\w+";
                    if (!matcher.matches())
                    {
                        f4 =false;
                        ((TextField)edFields.getField(fields[2])).setValue("1");
                        edFields.getField(fields[2]).addValidator(new RegexpValidator("\\d+","Field \"age\" don't consist symbols"));
                    }
                    else
                    {
                        if (Integer.parseInt((String)edFields.getField(fields[2]).getValue())>200 || Integer.parseInt((String)edFields.getField(fields[2]).getValue())<0)
                        {
                            f1 = false;
                            edFields.getField(fields[2]).addValidator(new IntegerRangeValidator("Age should be > 0 and < 200", 0, 200));
                        }
                    }
                }

                if (f1 && f2 && f3 && f4)
                {
                    User user = new User();
                    String id = ((String)edFields.getField(fields[0]).getValue());
                    String name =((String)edFields.getField(fields[1]).getValue());
                    String age;
                    if ((String)edFields.getField(fields[2]).getValue() == null)
                        age = "1";
                    else
                        age = ((String)edFields.getField(fields[2]).getValue());
                    String isAdmin =(String) setIsAdminBox.getValue();
                    user.setId(Integer.parseInt(id));
                    user.setName(name);
                    user.setAge(Integer.parseInt(age));
                    user.setAdmin(Boolean.parseBoolean(isAdmin));
                    user.setCreateDate(new GregorianCalendar());
                    userDao.updateUser(user);

                    table.removeAllItems();

                    List<User> users = userDao.getAllUsers();

                    for (User item : users)
                    {
                        table.addItem(new Object[]{item.getId(),item.getName(), item.getAge(), item.isAdmin(), item.getCreateDate().getTime()}, item.getId());

                    }
                }
            }
        });

        removeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                String id = ((String) edFields.getField(fields[0]).getValue());
                Object value = table.getValue();
                table.removeItem(value);
                userDao.deleteUser(Integer.parseInt(id));

            }
        });

    }
}
