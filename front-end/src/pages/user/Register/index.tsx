import {message} from 'antd';
import React from 'react';
import {history} from 'umi';
import Footer from '@/components/Footer';
import {register} from '@/services/ant-design-pro/api';
import styles from './index.less';
import {ProFormText,ProForm,StepsForm,} from '@ant-design/pro-form';


const Register: React.FC = () => {
 
  // 表单提交
  const handleSubmit = async (values: API.RegisterParams) => {
    const {userPassword, checkPassword} = values;
    // 校验
    if (userPassword !== checkPassword) {
      message.error('Password does not match!');
      return;
    }

    try {
      // 注册
      const id = await register(values);
      if (id) {
        const defaultLoginSuccessMessage = 'Register success!';
        message.success(defaultLoginSuccessMessage);

        /** 此方法会跳转到 redirect 参数所在的位置 */
        if (!history) return;
        const {query} = history.location;
        history.push({
          pathname: '/user/login',
          query,
        });
        return;
      }
    } catch (error: any) {
      const defaultLoginFailureMessage = 'Register failed!';
      message.error(defaultLoginFailureMessage);
    }
  };
  
  return (
    <div className={styles.container}>
      <div className={styles.content}>
      <StepsForm
          onFinish={async (values) => {
            await handleSubmit(values as API.RegisterParams);
          }}
        >
          <StepsForm.StepForm title="Step 1">
            <ProForm.Group>
              <ProFormText
                width="md"
                name="userAccount"
                label="Account"
                tooltip="Account must no less than 4 characters"
                placeholder="Enter your account here"
                rules={[
                  {
                    required: true,
                    message: 'Account is required!',
                  },
                ]}
              />
              <ProFormText.Password
                width="md"
                name="userPassword"
                label="Password"
                tooltip="Password must no less than 8 characters"
                placeholder="Create your password"
                rules={[
                  {
                    required: true,
                    message: 'Password is required!',
                  },
                ]}
              />
              <ProFormText.Password
                name="checkPassword"
                width="md"
                label="Confirm password"
                placeholder="Confirm password"
                rules={[
                  {
                    required: true,
                    message: 'Confirm password required!',
                  },
                  {
                    min: 8,
                    type: 'string',
                    message: 'Password must no shorter than 8',
                  },
                ]}
              />
            </ProForm.Group>
            {/* <ProForm.Group>
              <ProFormText.Password
                name="checkPassword"
                width="md"
                label="Confirm password"
                placeholder="Confirm password"
                rules={[
                  {
                    required: true,
                    message: 'Confirm password required!',
                  },
                  {
                    min: 8,
                    type: 'string',
                    message: 'Password must no shorter than 8',
                  },
                ]}
              />
              <ProFormDateRangePicker
                width="md"
                name={['contract', 'createTime']}
                label="合同生效时间"
              />
            </ProForm.Group> */}
          </StepsForm.StepForm>
          <StepsForm.StepForm title="Step 2">
            <ProForm.Group>
              <ProFormText
                  width="md"
                  name="username"
                  label="Username"
                  placeholder="Create your username here"
                  rules={[
                    {
                      required: true,
                      message: 'Username is required!',
                    },
                  ]}
                />
                <ProFormText
                  width="md"
                  name="email"
                  label="Email"
                  placeholder="Enter your email here"
                  rules={[
                    {
                      required: true,
                      message: 'Email is required!',
                    },
                  ]}
                />
                <ProFormText
                  width="md"
                  name="phone"
                  label="Mobile number"
                  placeholder="Enter your mobile number here"
                  rules={[
                    {
                      required: true,
                      message: 'Mobile number is required!',
                    },
                  ]}
                />
            </ProForm.Group>
          </StepsForm.StepForm>
          <StepsForm.StepForm title="Step 3">
            {/* // 可以用来上传头像 */}
            
          </StepsForm.StepForm>
        </StepsForm>
      </div>
      <Footer/>
    </div>
  );
};

export default Register;
