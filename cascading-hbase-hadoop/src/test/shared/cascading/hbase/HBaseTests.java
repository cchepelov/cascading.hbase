/*
 * Copyright (c) 2007-2015 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.hbase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cascading.flow.FlowConnector;
import cascading.hbase.helper.FlowConnectorFactory;
import cascading.property.AppProps;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class HBaseTests
  {

  /** The configuration. */
  protected static Configuration configuration;

  private static HBaseTestingUtility utility;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception
    {
    System.setProperty( HBaseCommonTestingUtility.BASE_TEST_DIRECTORY_KEY, "build/test-data" );
    utility = new HBaseTestingUtility();
    utility.startMiniCluster( 1 );
    configuration = utility.getConfiguration();
    }

  protected static void deleteTable( Configuration configuration, String tableName ) throws IOException
    {
    HBaseAdmin hbaseAdmin = new HBaseAdmin( configuration );
    if( hbaseAdmin.tableExists( Bytes.toBytes( tableName ) ) )
      {
      hbaseAdmin.disableTable( Bytes.toBytes( tableName ) );
      hbaseAdmin.deleteTable( Bytes.toBytes( tableName ) );
      }
    hbaseAdmin.close();
    }

  @AfterClass
  public static void tearDownAfterClass() throws Exception
    {
    if( utility != null )
      utility.shutdownMiniCluster();
    }

  public FlowConnector createHadoopFlowConnector()
    {
    return createHadoopFlowConnector( new HashMap<Object, Object>(  ) );
    }

  public FlowConnector createHadoopFlowConnector( Map<Object, Object> props )
    {
    Map<Object, Object> finalProperties = new Properties();
    finalProperties.putAll( props );
    finalProperties.put( HConstants.ZOOKEEPER_CLIENT_PORT, String.valueOf( utility.getZkCluster().getClientPort() ) );
    AppProps.setApplicationName( finalProperties, getClass().getName() );

    return FlowConnectorFactory.createFlowConnector( finalProperties );
    }

  }
