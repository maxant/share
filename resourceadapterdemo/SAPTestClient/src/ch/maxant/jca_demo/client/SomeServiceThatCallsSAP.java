package ch.maxant.jca_demo.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.resource.ResourceException;
import javax.sql.DataSource;

import ch.maxant.jca_demo.sapresourceadapter.SAPConnection;
import ch.maxant.jca_demo.sapresourceadapter.SAPConnectionFactory;

@Stateless 
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
public class SomeServiceThatCallsSAP {

    private final Logger log = Logger.getLogger(this.getClass().getName()); 
            
    @Resource(mappedName = "java:/eis/SAPResourceAdapter")
    private SAPConnectionFactory factory;

    @Resource(name = "java:/jdbc/MyXaDS")
    private DataSource ds;
    
    @Resource
    private SessionContext ctx;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String doSomethingInvolvingSeveralResources(String name) throws ResourceException, SQLException {
        try(SAPConnection sap = factory.getConnection()){
            try(Connection database = ds.getConnection(); 
                    PreparedStatement statement = database.prepareStatement("insert into temp.temp2 values (?,?)")
                    ){
                statement.setString(1, "FIRST_" + new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss.SSS").format(new Date()));
                statement.setInt(2, new Random().nextInt(4000));
                int cnt = statement.executeUpdate();
                log.log(Level.INFO, "wrote to db... " + cnt);
            }

            String sapResponse = sap.helloWorld(name);
            log.log(Level.INFO, "updated sap...");

            try(Connection database = ds.getConnection(); 
                PreparedStatement statement = database.prepareStatement("insert into temp.child values (null,?)")
            ){
                if("FAILDB".equals(name)){
                    statement.setInt(1, 3); //fails with FK Constraint exception
                }else{
                    statement.setInt(1, 1);
                }
                int cnt = statement.executeUpdate();
                log.log(Level.INFO, "wrote to db... " + cnt);
            }

            log.log(Level.INFO, "returning " + sapResponse);
            return sapResponse;
        }catch(SQLException e){
            //have to catch SQLException explicitly since 
            //its considered to be an application exception 
            //since it inherits from Exception and not 
            //RuntimeException - kinda sucks really.
            ctx.setRollbackOnly();
            throw e;
        }
    }
    
}
