import {
  LockOutlined,
  UserOutlined,
} from '@ant-design/icons';
import {Alert, Divider, message, Space, Tabs} from 'antd';
import React, {useState} from 'react';
import {ProFormCheckbox, ProFormText, LoginForm} from '@ant-design/pro-form';
import {history, useModel} from 'umi';
import {PLANET_LINK} from '@/constants';
import Footer from '@/components/Footer';
import {login} from '@/services/ant-design-pro/api';
import styles from './index.less';
import {Link} from "@umijs/preset-dumi/lib/theme";

const LoginMessage: React.FC<{
  content: string;
}> = ({content}) => (
  <Alert
    style={{
      marginBottom: 24,
    }}
    message={content}
    type="error"
    showIcon
  />
);

const Login: React.FC = () => {
  const [userLoginState] = useState<API.LoginResult>({});
  const [type, setType] = useState<string>('account');
  const {initialState, setInitialState} = useModel('@@initialState');

  const fetchUserInfo = async () => {
    const userInfo = await initialState?.fetchUserInfo?.();

    if (userInfo) {
      await setInitialState((s) => ({...s, currentUser: userInfo}));
    }
  };

  const handleSubmit = async (values: API.LoginParams) => {
    try {
      // 登录
      const user = await login({...values, type});

      if (user) {
        const defaultLoginSuccessMessage = 'Login success!';
        message.success(defaultLoginSuccessMessage);
        await fetchUserInfo();
        /** 此方法会跳转到 redirect 参数所在的位置 */

        if (!history) return;
        const {query} = history.location;
        const {redirect} = query as {
          redirect: string;
        };
        history.push(redirect || '/');
        return;
      }
    } catch (error) {
      const defaultLoginFailureMessage = 'Login failed!';
      message.error(defaultLoginFailureMessage);
    }
  };

  const {status, type: loginType} = userLoginState;
  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <LoginForm
          logo={<img alt="logo" src="/assets/logo1.png"/>}
          title="Insights"
          subTitle={<a href={PLANET_LINK} target="_blank" rel="noreferrer">The best personal project</a>}
          initialValues={{
            autoLogin: true,
          }}
          onFinish={async (values) => {
            await handleSubmit(values as API.LoginParams);
          }}
        >
          <Tabs activeKey={type} onChange={setType}>
            <Tabs.TabPane key="account" tab={'Login with your account'}/>
          </Tabs>
          {status === 'error' && loginType === 'account' && (
            <LoginMessage content={'Account or password error'}/>
          )}
          {type === 'account' && (
            <>
              <ProFormText
                name="userAccount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined className={styles.prefixIcon}/>,
                }}
                placeholder="Enter your account here"
                rules={[
                  {
                    required: true,
                    message: 'Account required!',
                  },
                  {
                    min: 4,
                    type: 'string',
                    message: 'Account length must above 4',
                  },
                ]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon}/>,
                }}
                placeholder="Enter your password here"
                rules={[
                  {
                    required: true,
                    message: 'Password required!',
                  },
                  {
                    min: 8,
                    type: 'string',
                    message: 'Password length must above 8',
                  },
                ]}
              />
            </>
          )}
          <div
            style={{
              marginBottom: 24,
            }}
          >
            <Space style={{ display: 'flex', justifyContent: 'space-between', width: '100%' }} split={<Divider type="vertical" />}>
              <ProFormCheckbox noStyle name="autoLogin">
                Remember me
              </ProFormCheckbox>
              <div>
                <Link to="/user/register">Register</Link>
              </div>
              
              {/* <a
                style={{
                  float: 'right',
                }}
                href={PLANET_LINK}
                target="_blank"
                rel="noreferrer"
              >
                Forgot Password?
              </a> */}
            </Space>
          </div>
        </LoginForm>
      </div>
      <Footer/>
    </div>
  );
};

export default Login;
