
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.cluster;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.collections.Seq;

/**
 * ClusterManager interface.
 */
public interface ClusterManager<T> {

    //~ Methods ......................................................................................................................................

    /** Call remote method. */
    <K> K callRemoteMethod(Class<?> clazz, T to, String methodName, Object[] args, @Nullable Class<?>[] types, Object options)
        throws Exception;

    /** Call remote method. */
    <K> K callRemoteMethods(Class<?> clazz, List<T> tos, String methodName, Object[] args, @Nullable Class<?>[] types, Object options)
        throws Exception;

    /** De Register MessageHandler for scope. */
    void deRegisterMessageHandler(int scope);

    /** Register MessageHandler for scope. */
    void registerMessageHandler(MessageHandler<?> handler);

    /** Register dispatcher. */
    void registerRpcDispatcher(Class<?> type, RpcDispatcher dispatcher);

    /** Send message for scope. */
    void sendMessage(int indexScope, Serializable object)
        throws Exception;

    /** Start the ClusterManager. */
    void start()
        throws Exception;

    /** Stop the ClusterManager. */
    void stop();

    /** Returns the cluster name. */
    String getClusterName();

    /** Returns the current address member as an String.* */
    String getCurrentMember();

    /** Returns the current address member as String.* */
    String getCurrentMemberId();

    /** @return  if the node is alive. */
    boolean isAlive(@NotNull String nodeName);

    /** Get Master node. */
    T getMaster();

    /** Get current member. */
    T getMember();

    /** Get Current member name. */
    String getMemberName();
    /** Get Member name of node T. */
    String getMemberName(T address);

    /** Ger Member addresses. */
    ImmutableList<T> getMembersAddresses();

    /** Return a Set of Member Addreses names. */
    default Seq<String> getMembersAddressNames() {
        return getMembersAddresses().map(a -> a == null ? "NA" : a.toString());
    }

    /** Return member UUID. */
    String getMemberUUID();

    /** GetPhysical Address. */
    @Nullable InetAddress getPhysicalAddress(T address);

    /** Returns true if the current member is Master.* */
    boolean isMaster();
}  // end interface ClusterManager
