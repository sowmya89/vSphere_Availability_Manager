import com.vmware.vim25.mo.*;

import java.io.FileWriter;

public class PStatistics extends Thread {

    ServiceInstance si = null;

    PStatistics(ServiceInstance si) {
        this.si = si;
    }

    public void run(){
        while(true){
            try {
                ManagedEntity[] hosts = new InventoryNavigator(si.getRootFolder()).searchManagedEntities("HostSystem");
                FileWriter writer = new FileWriter("E:/test4.csv");

                writer.append("Name");
                writer.append(',');
                writer.append("CPU Number");
                writer.append(',');
                writer.append("CPU Usage (max)");
                writer.append(',');
                writer.append("Overall CPU usage");
                writer.append(',');
                writer.append("Memory (Max)");
                writer.append(',');
                writer.append("Current Memory usage");
                writer.append("\n");

                for(int i=0; i<hosts.length; i++){

                    HostSystem h = (HostSystem) hosts[i];
                    if(h != null){
                        if(h.getName() != null){
                            VirtualMachine vms[] = h.getVms();

                            for (int p = 0; p < vms.length; p++) {
                                VirtualMachine vm = (VirtualMachine) vms[p];
                                if(vm != null)
                                {
                                    {
                                        String j;
                                        Integer m = vm.getConfig().getHardware().numCPU;
                                        
                                        if( vm.getRuntime().getMaxCpuUsage() == null)
                                            j = "null";
                                        else
                                            j=vm.getRuntime().getMaxCpuUsage().toString();
                                        Integer k = vm.getSummary().quickStats.overallCpuUsage;
                                        Integer l =vm.getSummary().quickStats.getHostMemoryUsage();
                                        Integer o =vm.getSummary().quickStats.getHostMemoryUsage();
                                        Integer n=vm.getConfig().getHardware().getMemoryMB();

                                        writer.append(vm.getName());
                                        writer.append(',');
                                        writer.append(m.toString());
                                        writer.append(',');
                                        writer.append(j);
                                        writer.append(',');
                                        writer.append(k.toString());
                                        writer.append(',');
                                        writer.append(n.toString());
                                        writer.append(',');
                                        writer.append(l.toString());
                                        writer.append("\n");
                                    }
                                }
                                writer.flush();
                            }
                        }
                    }
                }
                writer.close();
            } catch (Exception e) {
                System.out.println("Error in SnapShotManager: "+ e.getMessage());
            }

            try{
                Thread.sleep(10 * 1000);
            } catch(Exception e){
                System.out.println("Error in creating new Snapshot: " + e.getMessage());
            }
        }
    }
}
