

import java.rmi.RemoteException;
import java.util.*;

import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class RegisterVM {
	
	public static void registerVM_toHost(Datacenter dc, Folder rootFolder, String destHostIp, HostSystem deadHost) throws InvalidProperty, RuntimeFault, RemoteException, InterruptedException {
		ArrayList<String> deadVMNames = new ArrayList<String>();
        String vmName = "";
        System.out.println(dc.getName()+"---"+rootFolder.getName()+"---"+destHostIp+"---"+deadHost.getName());
        ManagedEntity[] deadVM = new InventoryNavigator(deadHost).searchManagedEntities("VirtualMachine");
        
        for(int j=0;j<deadVM.length;j++){
            System.out.println(deadVM[j].getName());
            deadVMNames.add(deadVM[j].getName());
        }
        HostSystem destHost = (HostSystem)new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem",destHostIp);
        System.out.println("--------------"+destHost.getName());
//        HostSystem hsn = (HostSystem)destHost;
//        System.out.println("--------------"+hsn.getName());
        
        System.out.println("Sleeping Thread from registerVM");
        //Thread.sleep(120000);
        ComputeResource cr = (ComputeResource) destHost.getParent();
        ResourcePool rp = cr.getResourcePool();
        Task disconnectHost = ((HostSystem) deadHost).getParent().destroy_Task();
        if (disconnectHost.waitForTask() == Task.SUCCESS)
        {
            System.out.println("disconnected");
            for(int i=0;i<deadVMNames.size();i++){
                vmName = deadVMNames.get(i);
                if(!(vmName.contains("TPLATE"))){
                    String vmxPath = "[nfs4team08]" + vmName + "/" + vmName + ".vmx";
                    Task registerVM = dc.getVmFolder().registerVM_Task(vmxPath, vmName, false, rp, destHost);
                    if (registerVM.waitForTask() == Task.SUCCESS) {
                        System.out.println("VM moved to another host");
                    }
//                    VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", deadVM[i].getName());
//                    Task task=vm.powerOnVM_Task(null);
//                    if(task.waitForMe()==Task.SUCCESS)
//                    {
//                        System.out.println(deadVM[i].getName() + " powered on");
//                    }
                }                
            }
        }
        Thread.sleep(2000);
    }
}
