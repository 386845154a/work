/*
 * Copyright 2013 gitblit.com.
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
package com.service.service.git;

import com.service.service.biz.TaskBiz;
import com.service.service.entity.TaskEntity;
import com.service.service.entity.UserModel;
import com.service.service.managers.IWorkHub;
import com.service.service.transport.git.GitDaemonClient;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.resolver.FileResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * 解决存储库并授予出口访问权。
 *
 * @author James Moger
 *
 */
public class RepositoryResolver<X> extends FileResolver<X> {

	private final Logger logger = LoggerFactory.getLogger(RepositoryResolver.class);

	private final IWorkHub gitblit;

	@Autowired
	public RepositoryResolver(IWorkHub gitblit) {
		super(gitblit.getRepositoriesFolder(), true);
		this.gitblit = gitblit;
	}

	/**
	 * 打开存储库并将存储库名称注入到设置中。
	 */
	@Override
	public Repository open(final X req, final String name)
			throws RepositoryNotFoundException, ServiceNotEnabledException {
		Repository repo = super.open(req, name);

		// Set repository name for the pack factories
		// We do this because the JGit API does not have a consistent way to
		// retrieve the repository name from the pack factories or the hooks.
		if (req instanceof HttpServletRequest) {
			// http/https request
			HttpServletRequest client = (HttpServletRequest) req;
			client.setAttribute("gitblitRepositoryName", name);
		} else if (req instanceof GitDaemonClient) {
			// git request
			GitDaemonClient client = (GitDaemonClient) req;
			client.setRepositoryName(name);
		}
		return repo;
	}

	/**
	 * 检查这个存储库是否可以由所请求的客户端连接来服务。
	 */
	@Override
	protected boolean isExportOk(X req, String repositoryName, Repository db) throws IOException {
		TaskEntity model = gitblit.getRepositoryModel(repositoryName);

		UserModel user = UserModel.ANONYMOUS;
		String scheme = null;
		String origin = null;

		if (req instanceof GitDaemonClient) {
			// git daemon request
			// this is an anonymous/unauthenticated protocol
			GitDaemonClient client = (GitDaemonClient) req;
			scheme = "git";
			origin = client.getRemoteAddress().toString();
		} else if (req instanceof HttpServletRequest) {
			// http/https request
			HttpServletRequest client = (HttpServletRequest) req;
			scheme = client.getScheme();
			origin = client.getRemoteAddr();
			user = gitblit.authenticate(client);
			if (user == null) {
				user = UserModel.ANONYMOUS;
			}
		}

		// TODO 影响了PUSH
		if (user.canClone(model)) {
			// user can access this git repo
			logger.debug(MessageFormat.format("{0}:// access of {1} by {2} from {3} 允许",
					scheme, repositoryName, user.getUserId(), origin));
			return true;
		}

		// 无权访问该任务库
		logger.warn(MessageFormat.format("{0}:// access of {1} by {2} from {3} 拒绝",
				scheme, repositoryName, user.getUserId(), origin));
		return false;
	}
}
