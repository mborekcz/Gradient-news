/*
 * 2016 Royal Institute of Technology (KTH)
 *
 * LSelector is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.kth.news.sim;

import java.util.HashMap;
import java.util.Map;
import se.kth.news.sim.compatibility.SimNodeIdExtractor;
import se.kth.news.system.HostMngrComp;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.SetupEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.kompics.simulator.network.identifier.IdentifierExtractor;
import se.sics.ktoolbox.omngr.bootstrap.BootstrapServerComp;
import se.sics.ktoolbox.util.identifiable.basic.IntIdentifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.overlays.id.OverlayIdRegistry;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import se.sics.ktoolbox.util.network.basic.BasicAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class ScenarioGen {
    public static final int appPort = 12345;
public static final	int nodeId = 100;

	static Operation killLeaderOp = new Operation<KillNodeEvent>() {
        @Override
        public KillNodeEvent generate() {
					return new KillNodeEvent() {
					        BasicAddress selfAdr;

					        {
								try {
										//selfAdr = BasicAddress(InetAddress.getByName("193.0.0.100"), 12345, new IntIdentifier(100));
					        	selfAdr = new BasicAddress(InetAddress.getByName("193.0.0." + nodeId), appPort, new IntIdentifier(nodeId));
								} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								}
					        }

					        @Override
					        public Address getNodeAddress() {
					                return selfAdr;
					        }

					        @Override
					        public String toString() {
					                return "KillPonger<" + selfAdr.toString() + ">";
					        }
					};
        }
};	
	
	
/**
	static Operation1 killPongerOp = new Operation1<KillNodeEvent, Integer>() {
        @Override
        public KillNodeEvent generate(final Integer self) {
                return new KillNodeEvent() {
                        TAddress selfAdr;

                        {
                                try {
                                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), 10000);
                                } catch (UnknownHostException ex) {
                                        throw new RuntimeException(ex);
                                }
                        }

                        @Override
                        public Address getNodeAddress() {
                                return selfAdr;
                        }

                        @Override
                        public String toString() {
                                return "KillPonger<" + selfAdr.toString() + ">";
                        }
                };
        }
};
*/
    static Operation<SetupEvent> systemSetupOp = new Operation<SetupEvent>() {
        @Override
        public SetupEvent generate() {
            return new SetupEvent() {
                @Override
                public void setupSystemContext() {
                    OverlayIdRegistry.registerPrefix("newsApp", ScenarioSetup.overlayOwner);
                }

                @Override
                public IdentifierExtractor getIdentifierExtractor() {
                    return new SimNodeIdExtractor();
                }
            };
        }
    };

    static Operation<StartNodeEvent> startBootstrapServerOp = new Operation<StartNodeEvent>() {

        @Override
        public StartNodeEvent generate() {
            return new StartNodeEvent() {
                KAddress selfAdr;

                {
                    selfAdr = ScenarioSetup.bootstrapServer;
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return BootstrapServerComp.class;
                }

                @Override
                public BootstrapServerComp.Init getComponentInit() {
                    return new BootstrapServerComp.Init(selfAdr);
                }
            };
        }
    };

    static Operation1<StartNodeEvent, Integer> startNodeOp = new Operation1<StartNodeEvent, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer nodeId) {
            return new StartNodeEvent() {
                KAddress selfAdr;

                {
                    selfAdr = ScenarioSetup.getNodeAdr(nodeId+100);
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return HostMngrComp.class;
                }

                @Override
                public HostMngrComp.Init getComponentInit() {
                    return new HostMngrComp.Init(selfAdr, ScenarioSetup.bootstrapServer, ScenarioSetup.newsOverlayId);
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    Map<String, Object> nodeConfig = new HashMap<>();
                    nodeConfig.put("system.id", nodeId+100);
                    nodeConfig.put("system.seed", ScenarioSetup.getNodeSeed(nodeId+100));
                    nodeConfig.put("system.port", ScenarioSetup.appPort);
                    return nodeConfig;
                }
            };
        }
    };
    static Operation1<StartNodeEvent, Integer> startNodeOp2 = new Operation1<StartNodeEvent, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer nodeId) {
            return new StartNodeEvent() {
                KAddress selfAdr;

                {
                    selfAdr = ScenarioSetup.getNodeAdr(nodeId);
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return HostMngrComp.class;
                }

                @Override
                public HostMngrComp.Init getComponentInit() {
                    return new HostMngrComp.Init(selfAdr, ScenarioSetup.bootstrapServer, ScenarioSetup.newsOverlayId);
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    Map<String, Object> nodeConfig = new HashMap<>();
                    nodeConfig.put("system.id", nodeId);
                    nodeConfig.put("system.seed", ScenarioSetup.getNodeSeed(nodeId));
                    nodeConfig.put("system.port", ScenarioSetup.appPort);
                    return nodeConfig;
                }
            };
        }
    };

    public static SimulationScenario simpleBoot() {
        SimulationScenario scen = new SimulationScenario() {
            {
                StochasticProcess systemSetup = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, systemSetupOp);
                    }
                };
                StochasticProcess startBootstrapServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startBootstrapServerOp);
                    }
                };
                StochasticProcess startPeers = new StochasticProcess() {
                    {
//                        eventInterArrivalTime(uniform(0, 0));
                        eventInterArrivalTime(uniform(0, 5));
//                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(100, startNodeOp, new BasicIntSequentialDistribution(1));
                    }//How many peers are in the simulation
                };
                StochasticProcess startPeers2 = new StochasticProcess() {
                    {
//                        eventInterArrivalTime(uniform(0, 0));
                        eventInterArrivalTime(uniform(0, 5));
//                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(5, startNodeOp2, new BasicIntSequentialDistribution(1));
                    }//How many peers are in the simulation
                };
                SimulationScenario.StochasticProcess killLeader = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(1, killLeaderOp);
                    }  
                };

                systemSetup.start();
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPeers.startAfterTerminationOf(1000, startBootstrapServer);
                //startPeers2.startAfterTerminationOf(1000*60, startPeers);
                //killLeader.startAfterTerminationOf(1000*300, startPeers);
                terminateAfterTerminationOf(1000*900, startPeers);
            }
        };

        return scen;
    }
}
